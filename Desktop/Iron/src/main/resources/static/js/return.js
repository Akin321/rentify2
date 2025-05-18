function approve(button){
		const id=button.getAttribute("data-returnId");
		 Swal.fire({
			    title: 'Are you sure?',
			    text: "Do you really want to approve this return request?",
			    icon: 'warning',
			    showCancelButton: true,
			    confirmButtonColor: '#28a745',  // Green
			    cancelButtonColor: '#d33',      // Red
			    confirmButtonText: 'Yes, approve it!',
			    cancelButtonText: 'Cancel'
			  }).then((result) => {
			    if (result.isConfirmed) {
			  
			    	window.location.href = `/request/approve?requestId=${id}`;

			    }
			  });
			}
		
	function reject(button){
		const id=button.getAttribute("data-returnId");
		 Swal.fire({
			    title: 'Are you sure?',
			    text: "Do you really want to reject this return request?",
			    icon: 'warning',
			    showCancelButton: true,
			    confirmButtonColor: '#d33',  // Red for reject
			    cancelButtonColor: '#3085d6',
			    confirmButtonText: 'Yes, reject it!',
			    cancelButtonText: 'Cancel'
			  }).then((result) => {
			    if (result.isConfirmed) {
			      // Ask for reason after confirmation
			      Swal.fire({
			        title: 'Reject Reason',
			        input: 'textarea',
			        inputLabel: 'Please provide a reason:',
			        inputPlaceholder: 'Enter reason for rejection...',
			        showCancelButton: true,
			        confirmButtonText: 'Submit',
			        cancelButtonText: 'Cancel',
			        inputValidator: (value) => {
			          if (!value) {
			            return 'You must provide a reason!';
			          }
			        }
			      }).then((reasonResult) => {
			        if (reasonResult.isConfirmed) {
			          const reason = reasonResult.value;

			          // Send the reason and ID to backend using POST (or modify as needed)
			          fetch('/request/reject', {
			            method: 'POST',
			            headers: {
			              'Content-Type': 'application/x-www-form-urlencoded'
			            },
			            body: new URLSearchParams({
			              requestId: id,
			              reason: reason
			            })
			          })
			          .then(response => {
			            if (response.ok) {
			              Swal.fire('Rejected!', 'The return request has been rejected.', 'success')
			                .then(() => {
			                  // Reload or redirect after success
			                  location.reload();
			                });
			            } else {
			              Swal.fire('Error!', 'Something went wrong.', 'error');
			            }
			          });
			        }
			      });
			    }
			  });
			}
			