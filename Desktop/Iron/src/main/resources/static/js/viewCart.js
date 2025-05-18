function confirmRemove(anchor) {
  const url = anchor.getAttribute("data-url");
  const message = anchor.getAttribute("data-message") || "Are you sure you want to remove this item?";

  Swal.fire({
    title: 'Are you sure?',
    text: message,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#000',
    cancelButtonColor: '#aaa',
    confirmButtonText: 'Yes, remove it!'
  }).then((result) => {
    if (result.isConfirmed) {
      window.location.href = url;
    }
  });
}

function changeQty(button, delta, itemId) {
    // Get the current cart item ID from the data attribute or other logic
    const cartId = itemId; // or extract from the button if needed
    
    // Send an AJAX request to the backend
    fetch(`/cart/update-quantity?cartId=${cartId}&delta=${delta}`, {
        method: 'GET', // HTTP method, GET in this case
        headers: {
            'Content-Type': 'application/json',
        },
    })
    .then(response => {
        if (response.ok) {
            // Optionally, refresh the cart or update the UI here
            window.location.reload(); // Reload the page after updating the quantity
        } else {
            // Handle errors, show an error message, etc.
            alert('Error updating quantity!');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Something went wrong!');
    });
}

function checkCartBeforeOrder() {
  fetch('/cart/validate', {
    method: 'GET'
  })
  .then(res => res.json())
  .then(data => {
    if (data.valid) {
      // Everything is okay, redirect
      window.location.href = "/user/checkout";
    } else {
      // Show popup with error items
      showStockIssueModal(data.issues);
    }
  })	.catch(error => {
	   console.error('Error during validation:', error);
	   alert('Something went wrong while checking your cart. Please try again later.');
	 });
}

function showStockIssueModal(issues) {
	Swal.fire({
	  title: 'Stock Issue',
	  icon: 'warning',
	  html: `
	    <p>Some items have stock issues:</p>
	    <ul style="text-align:left">
	      ${issues.map(item => `<li>${item.name} (Size: ${item.size}) — Qty: ${item.cartQty}, Stock: ${item.stock}</li>`).join('')}
	    </ul>
	    <p>Click <strong>Fix</strong> to adjust or remove items, or <strong>Cancel</strong> to stay.</p>
	  `,
	  showCancelButton: true,
	  confirmButtonText: 'Fix',
	  cancelButtonText: 'Cancel'
	}).then(result => {
	  if (result.isConfirmed) {
	    fetch('/cart/fix-quantity', {
	      method: 'POST'
	    }).then(() => window.location.href="/user/checkout");
	  }
	});
	
	}
	
	
	function getCoupons() {
	    fetch('/getCoupons', {
	        method: "GET",
	        headers: {
	            'Content-Type': 'application/json',
	        },
	    })
	    .then(response => {
	        if (!response.ok) {
	            alert('Something went wrong!');
	            throw new Error('Network response was not ok');
	        }
	        return response.json(); // Parse the response body
	    })
	    .then(data => {
	        displayCoupons(data); // Pass the data to displayCoupons
	    })
	    .catch(error => {
	        console.error('Error:', error);
	        alert('Something went wrong!');
	    });
	}










function displayCoupons(data) {
    const couponList = document.getElementById('couponList');
    couponList.innerHTML = '';
	


    data.forEach((coupon, index) => {
        const html = `
            <div class="coupon">
                <label>
                    <input type="radio" name="selectedCoupon" value="${coupon.couponCode}">
                    <strong>${coupon.couponCode}</strong><br>
                    <div>${coupon.discountPer}%off</div>
                    <div>${coupon.description}</div>
                    <div>Expires on: ${coupon.expiryDate}</div>
                </label>
            </div>
        `;
        couponList.insertAdjacentHTML('beforeend', html);
    });

    document.getElementById('couponModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('couponModal').style.display = 'none';
}

function checkCoupon() {
    const keyword = document.getElementById('manualCoupon').value;

    fetch(`/checkCoupon?keyword=${encodeURIComponent(keyword)}`, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
			document.getElementById('invalidFeedback').style.display = 'block';
		}
        return response.json();
    })
    .then(data => {
		document.getElementById('invalidFeedback').style.display = 'none';

            alert("coupon applied") // true = searched match
       
    })
   
}


function applySelectedCoupon() {
    const selected = document.querySelector('input[name="selectedCoupon"]:checked');
    if (selected) {
        fetch(`/applyCoupon?coupon=${encodeURIComponent(selected.value)}`,{
			method:"GET",
			headers: {
			           'Content-Type': 'application/json'
			       }
			
		})
		.then(result=>{
			if(!result.ok){
				alert("Something went wrong cannot apply coupon")
			}
			else{
			

				   // Optionally reload after toast hides
				  window.location.reload()
			}
		})
        // Handle application logic
        closeModal();
    } else {
        alert("Please select a coupon");
    }
}

function cancelCoupons(){
	fetch('/cancelCoupon',{
		Method:'GET',
		headers: {
					  'Content-Type': 'application/json'
					}
	})
	.then(response=>{
		if(!response.ok){
			alert("something happened.cannot cancel coupon")
		}
		else{
			window.location.reload();
		}
	})
}
