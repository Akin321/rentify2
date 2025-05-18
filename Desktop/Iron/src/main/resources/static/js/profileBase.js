
  // Get the current URL or path
  const currentPath = window.location.pathname;

  // Define the links and their corresponding paths
  const links = {
      '/user/view-order': 'orders-link',
      '/user/view-profile': 'profile-link',
	  '/user/edit-profile': 'profile-link',
	  '/user/updatePassword': 'profile-link',
      '/user/view-address': 'address-link',
	  '/user/add-address': 'address-link',
	  '/user/edit-address/{id}': 'address-link',
      '/user/view-wallet': 'wallet-link',
	  '/user/refer':'refer-link',
      '/delete-account': 'delete-account-link'
  };

  // Loop through each link and add the "active" class if it matches the current path
  Object.keys(links).forEach(path => {
      if (currentPath === path) {
          document.getElementById(links[path]).classList.add('active');
      }
  });
  



