// static/js/navbar-dropdown.js


let categoriesLoaded = {
  Men: false,
  Women: false
};

function loadCategories(gender) {
	if (categoriesLoaded[gender]) return;
	const genderType = gender === "Men" ? "Male" : "Female"; // Only needed if used somewhere

  fetch(`/user/categories/${genderType}`)
    .then(res => res.json())
    .then(data => {
      const dropdown = document.getElementById(`${gender.toLowerCase()}PrdDropdown`);
	  dropdown.innerHTML += data.map(cat =>
	    `<li><a class="dropdown-item" href="/user/view-products/${cat.gender}/${cat.id}">${cat.name}</a></li>`
	  ).join('');
	  
	  categoriesLoaded[gender] = true;
    });
}



document.addEventListener("DOMContentLoaded", function () {
  document.getElementById("menDropdown").addEventListener("click", () => loadCategories("Men"));
  document.getElementById("womenDropdown").addEventListener("click", () => loadCategories("Women"));
  
});
document.getElementById('keyword').addEventListener('input', function() {
       var submitButton = document.getElementById('submitButton');
       if (this.value.trim() !== '') {
           submitButton.disabled = false;  // Enable button when input has value
       } else {
           submitButton.disabled = true;   // Disable button when input is empty
       }
   });


   document.addEventListener("DOMContentLoaded", function () {
     const successMessage = document.body.getAttribute("data-success-message");
     const errorMessage = document.body.getAttribute("data-error-message");

     toastr.options = {
       "closeButton": true,
       "progressBar": true,
       "positionClass": "toast-top-center",
       "timeOut": "3000"
     };

     if (successMessage) {
       toastr.success(successMessage);
     }

     if (errorMessage) {
       toastr.error(errorMessage);
     }
   });
      
