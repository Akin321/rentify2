
document.addEventListener("DOMContentLoaded", function () {
    const sizeRadios = document.querySelectorAll('input[name="size"]');
    const addToBagBtn = document.getElementById('addToBagBtn');

    sizeRadios.forEach(radio => {
      radio.addEventListener('change', () => {
        addToBagBtn.disabled = false;
      });
    });
  });