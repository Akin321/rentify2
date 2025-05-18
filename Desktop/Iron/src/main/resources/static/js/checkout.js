const radios = document.querySelectorAll('input[name="address"]');
  radios.forEach(radio => {
	  window.addEventListener('DOMContentLoaded', () => {
		  const selectedRadio = document.querySelector('input[name="address"]:checked');
		  if (selectedRadio) {
		    const selectedCard = selectedRadio.closest('.address-card');
		    selectedCard.classList.add('selected');
		    selectedCard.querySelector('.deliver-btn').style.display = 'block';
		  }
		});

    radio.addEventListener('change', () => {
      document.querySelectorAll('.address-card').forEach(card => {
        card.classList.remove('selected');
        card.querySelector('.deliver-btn').style.display = 'none';
      });
      const selectedCard = radio.closest('.address-card');
      selectedCard.classList.add('selected');
      selectedCard.querySelector('.deliver-btn').style.display = 'block';
    });
  });
  function setDeliveryAddress(button) {
	    const addressId = button.getAttribute("data-address-id");
	    const addressBox=document.querySelector(".address-box");
	    const addressMain=document.querySelector(".address-main");
	    const orders=document.querySelector(".order-main");
	    const addressCard = button.closest(".address-card");
	    
	 // Extract name, full address, and pincode
	    const name = addressCard.querySelector(".address-name")?.textContent.trim();
	    const fullAddress = addressCard.querySelector(".address-details")?.textContent.trim();
	    
	    
	    addressBox.querySelector(".address-box-name").textContent = name;
	    addressBox.querySelector(".address-box-details").textContent=fullAddress;

	    fetch(`/user/select-address?addressId=${addressId}`, {
	        method: "GET" // Or "POST" if you prefer
	    })
	    .then(response => {
	        if (response.ok) {
	        	 addressBox.classList.remove("d-none");
	        	 addressMain.classList.add("d-none");
	        	orders.classList.remove("d-none");
	        } else {
	            alert("Failed to set delivery address");
	        }
	    });
	}
  const addAddressBtn = document.getElementById("addAddressBtn");
  const addressForm= document.getElementById("addressForm");
  const cancelBtn=document.getElementById("cancelBtn")
  
  addAddressBtn.addEventListener('click',()=>{
	  addAddressBtn.classList.add("d-none");
	  addressForm.classList.remove("d-none");
  });
  
  cancelBtn.addEventListener('click',(e)=>{
	  e.preventDefault();
	  addressForm.classList.add("d-none");
	  addAddressBtn.classList.remove("d-none");
  });
  



  document.getElementById('addressForm').addEventListener("submit", async function (e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);

    // Clear all previous errors
    document.querySelectorAll("[id^='error-']").forEach(div => div.textContent = '');

    try {
      const response = await fetch("/user/save-address", {
        method: "POST",
        body: formData
      });

      if (!response.ok) {
        const errors = await response.json();
        for (const field in errors) {
          const errorDiv = document.getElementById(`error-${field}`);
          if (errorDiv) {
            errorDiv.textContent = errors[field];
          }
        }
      } else {
        const data = await response.json();
       window.location.href="/user/checkout"

        form.reset();
      }
    } catch (err) {
      console.error("Error submitting form", err);
    }
  });
  
  document.querySelectorAll('.edit-address-btn').forEach(btn => {
	  btn.addEventListener('click', function () {
	    document.getElementById('edit-id').value = this.dataset.id;
	    document.getElementById('edit-name').value = this.dataset.name;
	    document.getElementById('edit-phone').value = this.dataset.phone;
	    document.getElementById('edit-address').value = this.dataset.address;
	    document.getElementById('edit-locality').value = this.dataset.locality;
	    document.getElementById('edit-landmark').value = this.dataset.landmark ?? '';
	    document.getElementById('edit-city').value = this.dataset.city;
	    document.getElementById('edit-state').value = this.dataset.state;
	    document.getElementById('edit-pincode').value = this.dataset.pincode;

	    // Set address type radio
	   

	    if (this.dataset.addresstype === "WORK") {
	      document.getElementById('edit-work').checked = true;
	    } else {
	      document.getElementById('edit-home').checked = true;
	    }

	    // Show modal
	    new bootstrap.Modal(document.getElementById('editAddressModal')).show();
	  });
	});

  document.getElementById('editAddressForm').addEventListener("submit", async function (e) {
	  console.log("Form submitted");
		e.preventDefault();
		 const form = e.target;
		 const formData = new FormData(form);
		 document.querySelectorAll("[id^='error-edit-']").forEach(div => div.textContent = '');

		    try {
		    	
		      const response = await fetch("/user/edit-address", {
		        method: "POST",
		        body: formData
		      });

		      if (!response.ok) {
		        const errors = await response.json();
		        for (const field in errors) {
		          const errorDiv = document.getElementById(`error-edit-${field}`);
		          if (errorDiv) {
		            errorDiv.textContent = errors[field];
		          }
		        }
		      } else {
		        const data = await response.json();
		      	window.location.href="/user/checkout";

		        form.reset();
		        const modal = bootstrap.Modal.getInstance(document.getElementById('editAddressModal'));
		        modal.hide();
		      }
		    } catch (err) {
		      console.error("Error submitting form", err);
		    }
		  });
		  
  document.addEventListener('DOMContentLoaded', function () {
	    const toggleBtn = document.getElementById('toggle-addresses');
	    const arrowIcon = document.getElementById('arrow-icon');
	    const hiddenAddresses = document.querySelectorAll('.hidden-address');

	    toggleBtn?.addEventListener('click', function () {
	      hiddenAddresses.forEach(card => card.classList.toggle('d-none'));
	      toggleBtn.querySelector('span').textContent =
	        toggleBtn.querySelector('span').textContent === 'Show More' ? 'Show Less' : 'Show More';
	      arrowIcon.classList.toggle('rotate');
	    });
	  });
		
  function openAddressCard(button) {
	    const address = document.querySelector(".address-main");
	    const addressBox = document.querySelector(".address-box");
	    const orderSummary=document.querySelector(".order-main");
	    const paymentSummary=document.querySelector(".payment-details");
	    const changeOrderBtn=document.querySelector(".change-order-btn");


	    paymentSummary.classList.add("d-none");
	    changeOrderBtn.classList.add("d-none");


	    address.classList.remove("d-none");
	    addressBox.classList.add("d-none");
	    orderSummary.classList.add("d-none");
	}
  
  function openOrderCard(button) {
	    const paymentSummary=document.querySelector(".payment-details");
	    const orderSummary=document.querySelector(".order-main");
	    const changeOrderBtn=document.querySelector(".change-order-btn");

	    paymentSummary.classList.add("d-none");
	    changeOrderBtn.classList.add("d-none");
	    orderSummary.classList.remove("d-none");
	}
	
  function startRazorpayPayment() {
	  fetch('/user/api/payment/order', {
	    method: 'Get',
	   
	  })
	  .then(res => res.json())
	  .then(orderData => {
	    var options = {
	      "key": "rzp_test_OmCIXPHWUDWbjB", // Public key
	      "amount": orderData.amount,
	      "currency": orderData.currency,
	      "name": "Iron Pvt Ltd",
	      "description": "Order Payment",
	      "order_id": orderData.id,
	      "handler": function (response) {
	        // ✅ Send this response to backend for verification
	    	  verifyPayment(response)      },
	      "theme": { "color": "#000000" }
	    };
	    var rzp = new Razorpay(options);
	    rzp.open();
	  })
	  .catch(error => {
	    console.error("Payment error:", error);
	    alert("Payment initiation failed.");
	  });
	}

	
	function proceedToPayment(){
		 const orderSummary=document.querySelector(".order-main");
		    const paymentSummary=document.querySelector(".payment-details");
		    const changeOrderBtn=document.querySelector(".change-order-btn");
		
		fetch('/cart/validate',{
			method:'Get'
		})
		.then(response=>response.json())
		.then(data=>{
			if(data.valid){
				 orderSummary.classList.add("d-none");
				    paymentSummary.classList.remove("d-none");
			    	changeOrderBtn.classList.remove("d-none");
			}
			else{
				showStockError();
			}
		}).catch(error => {
			   console.error('Error during validation:', error);
			   alert('Something went wrong while checking your cart. Please try again later.');
			 });

	   

	   

	}
	
	function showStockError() {
		  Swal.fire({
		    icon: 'error',
		    title: 'Stock Issue Detected',
		    html: `
		      Some items in your cart are <strong>out of stock</strong> or exceed the available quantity.<br><br>
		      Please <strong>review your cart</strong> to remove or adjust those items before proceeding.
		    `,
		    confirmButtonText: 'Go Back to Cart',
		    confirmButtonColor: '#d33',
		    backdrop: true,
		    allowOutsideClick: false
		  }).then(()=>{
			  window.location.href="/view-cart"
		  });
		}
	function verifyPayment(response) {
		  fetch('/user/api/payment/verify', {
		    method: 'POST',
		    headers: {
		      'Content-Type': 'application/json'
		    },
		    body: JSON.stringify(response)
		  })
		  .then(res => res.json())
		  .then(data => {
		    if (data.verified) {
		    	
			       orderSuccessPopup();

		      // Redirect or show order success
		    } else {
			      orderfailedPopup();
		    }
		  })
		  .catch(err => {
		    console.error("Verification error:", err);
		    alert("Server error during verification.");
		  });
		}
		
		function togglePaymentButtons() {
		  document.getElementById("razorpaySection").classList.add("d-none");
		  document.getElementById("codSection").classList.add("d-none");
		  document.getElementById("walletSection").classList.add("d-none");

		  if (document.getElementById("razorpay").checked) {
		    document.getElementById("razorpaySection").classList.remove("d-none");
		  } else if (document.getElementById("cod").checked) {
		    document.getElementById("codSection").classList.remove("d-none");
		  } else if (document.getElementById("wallet").checked) {
		    document.getElementById("walletSection").classList.remove("d-none");
		  }
		}
	 togglePaymentButtons();
	 
	 function confirmCOD() {
	   Swal.fire({
	     title: 'Confirm Cash on Delivery',
	     text: 'Do you want to proceed with Cash on Delivery?',
	     icon: 'question',
	     showCancelButton: true,
	     confirmButtonText: 'Yes, Confirm COD',
	     cancelButtonText: 'Cancel',
	     confirmButtonColor: '#28a745',
	     cancelButtonColor: '#d33'
	   }).then((result) => {
	     if (result.isConfirmed) {
	       fetch('/user/order/cod/verify', {
	         method: 'GET',
	         headers: {
	           'Content-Type': 'application/json'
	         }
	       })
	         .then(res => res.json())
	         .then(data => {
	           if (data.verified) {
	             orderSuccessPopup(); // show success alert or redirect
	           } else {
	             orderfailedPopup(); // show failure alert
	           }
	         })
	         .catch(err => {
	           console.error("Verification error:", err);
	           Swal.fire({
	             icon: 'error',
	             title: 'Server Error!',
	             text: 'Please try again later.'
	           });
	         });
	     }
	   });
	 }

		  
		  function payWithWallet(button) {
		    const amount = button.getAttribute("data-amount");

		    Swal.fire({
		      title: 'Are you sure?',
		      text: 'Do you want to pay using your wallet?',
		      icon: 'question',
		      showCancelButton: true,
		      confirmButtonColor: '#3085d6',
		      cancelButtonColor: '#d33',
		      confirmButtonText: 'Yes, pay with wallet'
		    }).then((result) => {
		      if (result.isConfirmed) {
		        fetch('/user/wallet/pay', {
		          method: 'POST',
		          headers: {
		            'Content-Type': 'application/json'
		          },
		          body: JSON.stringify({ amount: amount }) // sending amount as a JSON parameter
		        })
		        .then(response => {
		          if (response.ok) {
		            orderSuccessPopup();
		          } else {
		            orderfailedPopup();
		          }
		        })
		        .catch(error => {
		          Swal.fire('Error', error.message, 'error');
		        });
		      }
		    });
		  }

			

	 	function orderSuccessPopup(){
	 		 Swal.fire({
	 	        icon: 'success',
	 	        title: 'Order Placed!',
	 	        text: 'Your order has been successfully placed.',
	 	        showCancelButton: true,
	 	        confirmButtonText: 'View Order Details',
	 	        cancelButtonText: 'Continue Shopping'
	 	      }).then((result) => {
	 	        if (result.isConfirmed) {
	 	          // Redirect to order details
	 	          window.location.href = "/user/view-order";
	 	        } else {
	 	          // Redirect to homepage or shop
	 	          window.location.href = "/user/home";
	 	        }
	 	      });
	 	}
		
	
	 	
	 	function orderfailedPopup(){
	 		Swal.fire({
	 	        icon: 'error',
	 	        title: 'Order Failed!',
	 	        text: 'There was an issue placing your order.',
	 	        showCancelButton: true,
	 	        confirmButtonText: 'Go to Cart',
	 	        cancelButtonText: 'Try Again'
	 	      }).then((result) => {
	 	        if (result.isConfirmed) {
	 	          window.location.href = "/view-cart";
	 	        } else {
	 	          // Optionally call `confirmCOD()` again or reload
	 	          location.reload();
	 	        }
	 	      });
	 	}
