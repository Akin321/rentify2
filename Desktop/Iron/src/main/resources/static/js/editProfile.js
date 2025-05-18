// static/js/navbar-dropdown.js


function openOtpModal() {
       document.getElementById("otpModal").style.display = "block";
       document.getElementById("overlay").style.display = "block";
       document.getElementById("otpMessage").innerText = '';
       document.getElementById("otpInput").value = '';
       document.getElementById("otpSection").style.display = "none";
   }

   function closeOtpModal() {
       document.getElementById("otpModal").style.display = "none";
       document.getElementById("overlay").style.display = "none";
	   fetch("/user/clear-otp-session", {
	           method: "POST"
	       })
	       .then(response => response.text())
	       .then(data => console.log(data))
	       .catch(error => console.error("Error clearing OTP session:", error));
	   
   }

   function sendOtpToNewEmail() {
       const newEmail = document.getElementById("newEmail").value;
	   const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

       if (!newEmail || !emailPattern.test(newEmail)) {
           alert("Please enter a valid email.");
           return;
       }

       fetch('/user/send-email-otp', {
           method: 'POST',
           headers: { 'Content-Type': 'application/json' },
           body: JSON.stringify({ email: newEmail })
		   
       })
       .then(res => res.text())
       .then(data => {
           if (data === 'success') {
               document.getElementById("otpSection").style.display = "block";
               document.getElementById("otpMessage").style.color = "green";
               document.getElementById("otpMessage").innerText = "OTP sent to " + newEmail;
           } else {
			 console.log(data);
			 console.log(newEmail);
               document.getElementById("otpMessage").style.color = "red";
               document.getElementById("otpMessage").innerText = "Failed to send OTP.";
           }
       });
   }

   function verifyNewEmailOtp() {
       const newEmail = document.getElementById("newEmail").value;
       const otp = document.getElementById("otpInput").value;

       fetch('/user/verify-email-otp', {
           method: 'POST',
           headers: { 'Content-Type': 'application/json' },
           body: JSON.stringify({ email: newEmail, otp: otp })
       })
       .then(res => res.text())
       .then(data => {
           if (data === 'verified') {
               const emailInput = document.getElementById("email");
              
               emailInput.value = newEmail;

             

               document.getElementById("otpsuccess").innerText = "Email changed successfully";
               closeOtpModal();
           } else {
               document.getElementById("otpMessage").style.color = "red";
               document.getElementById("otpMessage").innerText = data;
           }
       });
   }
