package com.example.demo.service;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.InvoiceDto;
import com.example.demo.Repository.AddressRepo;
import com.example.demo.Repository.CartRepo;
import com.example.demo.Repository.CouponRepo;
import com.example.demo.Repository.CouponUsageRepo;
import com.example.demo.Repository.OrderItemRepo;
import com.example.demo.Repository.OrderRepo;
import com.example.demo.Repository.OrderSpecifications;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Repository.ProductVarientRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.AddressModel;
import com.example.demo.model.CartModel;
import com.example.demo.model.CouponModel;
import com.example.demo.model.CouponUsage;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderModel;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.PaymentType;
import com.example.demo.model.ProductImages;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductVariant;

import jakarta.transaction.Transactional;

@Service
public class OrderService {
	
	@Autowired
	AddressRepo addressRepo;
	
	@Autowired
	CartService cartService;
	
	@Autowired
	CartRepo cartRepo;
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	ProductVarientRepo productVarientRepo;
	
	@Autowired
	OrderRepo orderRepo;
	
	@Autowired
	OrderItemRepo orderItemRepo;
	
	@Autowired
	CouponRepo couponRepo;
	
	@Autowired
	CouponUsageRepo couponUsageRepo;
	
	@Autowired
	WalletService walletService;

	public boolean verifySignature(Map<String, String> payload) {
		// TODO Auto-generated method stub
		String orderId = payload.get("razorpay_order_id");
        String paymentId = payload.get("razorpay_payment_id");
        String signature = payload.get("razorpay_signature");
        String data = orderId + "|" + paymentId;
        

        try {
            String generatedSignature = hmacSHA256(data, "GTIz8JEGgmfHFhQRBK0YZfB1");
            return generatedSignature.equals(signature);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
		
	}
        
        private String hmacSHA256(String data, String secret) throws Exception {
	        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
	        Mac mac = Mac.getInstance("HmacSHA256");
	        mac.init(secretKeySpec);
	        byte[] hash = mac.doFinal(data.getBytes());

	        // Convert bytes to hex
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : hash) {
	            String hex = Integer.toHexString(0xff & b);
	            if (hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    }
        @Transactional
		public void placeOrderWithPayment( NewUserModel user2, PaymentType paymenType, CouponModel coupon) {
			// TODO Auto-generated method stub
			 List<CartModel> cartItems = cartRepo.findByUser(user2);
			    BigDecimal totalMrp = cartService.getTotalMrp(cartItems);
			    BigDecimal totalDiscount=cartService.getTotalDiscount(cartItems);
			    BigDecimal preTotalAmount=totalMrp.subtract(totalDiscount);

			    BigDecimal couponDiscount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
				if(coupon!=null) {
					couponDiscount=cartService.getcouponDisount(coupon.getDiscountPer(),preTotalAmount);
				}
				BigDecimal totalOrderAmount=preTotalAmount.subtract(couponDiscount);
	        	 walletService.payWithWallet(preTotalAmount,user2);

			  
			    OrderModel order = new OrderModel();
			    order.setOrderDate(LocalDateTime.now());
			    if(coupon!=null) {
				    order.setCouponCode(coupon.getCouponCode());
				    order.setCouponAmount(couponDiscount);

			    }
			    

		
			    AddressModel address = addressRepo.findByUserAndIsDeliveryAddress(user2,true);

			 
			    String order_address = address.getName() + ", " +
			                           address.getAddress() + ", " +
			                           address.getLocality() + ", " +
			                           address.getCity() + ", " +
			                           address.getState() + "-" +
			                           address.getPincode() + "  Mobile:" +
			                           address.getPhone();

			    order.setAddress(order_address);

	
			    String uniqueOrderId = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
			    order.setOrderId(uniqueOrderId);

		
			    order.setPaymentType(paymenType);

			
			    order.setTotalAmount(totalOrderAmount);

			 
			    order.setUser(user2);
			    
			    List<OrderItem> orderItems = new ArrayList<>();
			    
			    for (CartModel cartItem : cartItems) {
			        OrderItem item = new OrderItem();

			        // Set color and fit
			        item.setColor(cartItem.getProduct().getColor());
			        item.setFitName(String.valueOf(cartItem.getProduct().getFit()));

			        // Get main image
			        String image = cartItem.getProduct().getImages().stream()
			            .filter(img -> Boolean.TRUE.equals(img.getIs_main()))
			            .map(ProductImages::getImage)
			            .findFirst()
			            .orElse(null);  // Or some default image string

			        item.setImage(image);

			        // Set order reference and status
			        item.setOrder(order);
			        item.setOrderStatus(OrderStatus.OrderPlaced);
			        item.setProduct(cartItem.getProduct());

			        // Set product details
			        item.setProductName(cartItem.getProduct().getName());
			        item.setProductType(cartItem.getProduct().getProductType().getName());

			        // Set quantity and size
			        item.setQuantity(cartItem.getQuantity());
			        item.setSize(String.valueOf(cartItem.getVariant().getSize()));

			        // Calculate pricing
			        BigDecimal basePrice = cartItem.getProduct().getBasePrice();
			        BigDecimal discount = cartItem.getProduct().getDiscountPer();
			        BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
			        BigDecimal discountPerUnit = basePrice.multiply(discount).divide(BigDecimal.valueOf(100));


			        BigDecimal prdTotalMrp = basePrice.multiply(quantity);
			        item.setTotalMRP(prdTotalMrp);
			        
			        BigDecimal prdTotalDiscount=discountPerUnit.multiply(quantity);
			        item.setTotalDiscount(prdTotalDiscount);

			        BigDecimal totalAmount = (prdTotalMrp.subtract(prdTotalDiscount));
			        item.setTotalAmount(totalAmount);

			        // Update product stock
			        ProductModel product = cartItem.getProduct();
			        ProductVariant variant=productVarientRepo.findByProductAndSize(product,cartItem.getVariant().getSize()).orElseThrow(()-> new ResourceNotFoundException("variant not found"));
			        variant.setStock(variant.getStock()-cartItem.getQuantity());
			        productVarientRepo.save(variant);
			        item.setVariant(variant);
			        // Add to orderItems list
			        orderItems.add(item);
			    }
			    order.setOrderItems(orderItems);
			    orderRepo.save(order);
			    if (coupon != null) {
			        // Update total usage of the coupon
			        int totalUsage = coupon.getTotal_usage();
			        coupon.setTotal_usage(totalUsage + 1);
			        couponRepo.save(coupon); // Save updated coupon

			        // Handle per-user coupon usage
			        CouponUsage couponUsage;
			        Optional<CouponUsage> couponUsageOpt = couponUsageRepo.findByUserAndCoupon(user2, coupon);

			        if (couponUsageOpt.isPresent()) {
			            // If the user has already used this coupon, increment the count
			            couponUsage = couponUsageOpt.get();
			            couponUsage.setUsage_count(couponUsage.getUsage_count() + 1);
			        } else {
			            // First time this user is using the coupon
			            couponUsage = new CouponUsage();
			            couponUsage.setCoupon(coupon);
			            couponUsage.setUsage_count(1);
			            couponUsage.setUser(user2);
			        }

			        // Save coupon usage
			        couponUsageRepo.save(couponUsage);
			    }

			    
			    
			   
			    cartService.clearUserCart(user2);

			
			
		}

	

		public Page<OrderItem> getOrders(List<String> status, String keyword, int page, int size, NewUserModel user) {
			// TODO Auto-generated method stub
			Pageable pageable= PageRequest.of(page, size,Sort.by("createdAt").descending());
		    Specification<OrderItem> spec = OrderSpecifications.filterBy(status, keyword, user);
		    return orderItemRepo.findAll(spec, pageable);


			
		}

		public Page<OrderModel> getAllOrders(int page, int size, String keyword) {
			// TODO Auto-generated method stub
			Pageable pageable= PageRequest.of(page, size,Sort.by("orderDate").descending());
			Page<OrderModel> orders;
			if(keyword==null || keyword.isEmpty()) {
				orders=orderRepo.findAll(pageable);
			}
			else {
				orders =orderRepo.findByOrderIdContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(keyword, keyword,pageable);

			}
			return orders;

		}

		public void updateStatus(Integer orderItemId, OrderStatus status) {
			// TODO Auto-generated method stub
			OrderItem orderItem=orderItemRepo.findById(orderItemId).orElseThrow(()->new ResourceNotFoundException("Resource Not Found"));
			if(orderItem.getOrderStatus()==OrderStatus.Delivered) {
				throw new IllegalArgumentException("Already Delivered");
			}
			orderItem.setOrderStatus(status);
			if(status==OrderStatus.Delivered) {
				orderItem.setDeliveryDate(LocalDateTime.now());

			}
			orderItemRepo.save(orderItem);
			
			
		}
		
		public InvoiceDto generateInvoice(int orderItemId) {
			OrderItem orderItem=orderItemRepo.findById(orderItemId).orElseThrow(()->new ResourceNotFoundException ("resource not found"));
			InvoiceDto invoiceDto=new InvoiceDto ();
				invoiceDto.setAddress(orderItem.getOrder().getAddress());
				invoiceDto.setDeliveryDate(orderItem.getDeliveryDate());
				invoiceDto.setDiscount(orderItem.getTotalDiscount());
				invoiceDto.setInvoiceNumber("INV"+UUID.randomUUID());
				invoiceDto.setOrderDate(orderItem.getOrder().getOrderDate());
				invoiceDto.setOrderId(orderItem.getOrder().getOrderId());
				invoiceDto.setPaymentMethod(String.valueOf(orderItem.getOrder().getPaymentType()));
				invoiceDto.setProductName(orderItem.getProductName());
				invoiceDto.setQuantity(orderItem.getQuantity());
				invoiceDto.setTotalamount(orderItem.getTotalAmount());
				invoiceDto.setTotalmrp(orderItem.getTotalMRP());
		        String logoPath = "file:////Iron/src/main/resources/static/images/logo.png";
		        invoiceDto.setLogo(logoPath);

			return invoiceDto;
		}

		public void updateProductAndStatus(int orderItemId) {
			// TODO Auto-generated method stub
			 OrderItem orderItem = orderItemRepo.findById(orderItemId)
			            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

			    ProductVariant variant = orderItem.getVariant(); 

			    if (variant == null) {
			        throw new IllegalStateException("variant is missing for this order item.");
			    }

			    variant.setStock(variant.getStock() + orderItem.getQuantity());
			    productVarientRepo.save(variant);

			    orderItem.setOrderStatus(OrderStatus.Cancelled);
			    orderItem.setCancelDate(LocalDateTime.now());
			    orderItemRepo.save(orderItem);
			
		}

		public BigDecimal refundAmount(int orderItemId) {
			// TODO Auto-generated method stub
			 OrderItem orderItem = orderItemRepo.findById(orderItemId)
			            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
			 PaymentType paymentType = orderItem.getOrder().getPaymentType();

			    if (paymentType!=PaymentType.CashOnDelivery) {
			    	System.out.println("entered amount");
			        return orderItem.getTotalAmount();
			    } else {
			        return BigDecimal.ZERO;
			    }
		}

	
}
