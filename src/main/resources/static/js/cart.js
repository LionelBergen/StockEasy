$(document).ready(function() {
    // If the cart icon is shown, update it to show cart amount
    if($('#shopping-cart').length) {
        $('#shopping-cart').attr('data-count', $('#cart_size').text());
    }

    if($('#navbar-right')) {
        $("#navbar-right").on('click', function(){
            const cartSize = $('#shopping-cart').attr('data-count');

            if (cartSize != 0) {
                window.location.href = '/cart';
            } else {
                window.location.href = '/cart';
            }
            return false;
        });
    }

    $(".remove_from_cart").on('click', function() {
        const tableRowClicked = $(this).closest('tr');

        const productId = $(tableRowClicked.find(".cart_product")).attr('data');
        const variantId = $(tableRowClicked.find(".cart_variant")).attr('data');
        const vendorId = $(tableRowClicked.find(".cart_vendor")).attr('data');

        console.log('removing product P: ' + productId + ' V: ' + variantId + ' ve: ' + vendorId);
        $.ajax({
            type : "GET",
            url : "/removeProduct",
            data :   {
                productId: productId,
                variantId: variantId,
                vendorId: vendorId
            },
            success: function(data){
                tableRowClicked.remove();
                if ($("#cart_product_table tbody tr").length == 0) {
                    window.location.href = '/';
                }
                updateCartTotal();
            },
            error: function(error) {
                console.error('ERROR');
                console.error(error);
            }
        });
    });

    $(".quantity_cart").on('change', function() {
        const index = $(this).attr('data-index');
        const row = $($("#cart_product_table tbody tr")[index]);

        const quantity = parseInt(this.value);
        const productId = $(row.find(".cart_product")).attr('data');
        const variantId = $(row.find(".cart_variant")).attr('data');
        const vendorId = $(row.find(".cart_vendor")).attr('data');

        $.ajax({
            type : "GET",
            url : "/changeQuantity",
            data :   {
                quantity: quantity,
                productId: productId,
                variantId: variantId,
                vendorId: vendorId
            },
            success: function(data){
                updateCartTotal();
            },
            error: function(error) {
                console.error('ERROR');
                console.error(error);
            }
        });
    });

    function updateCartTotal() {
        if ($("#cart_total")) {
            $("#cart_total").empty();

            let newSubtotal = 0.0;
            const tableRows = $("#cart_product_table tbody tr");

            for (let i=0; i<tableRows.length; i++) {
                const quantityValue = $(tableRows[i]).find('.quantity').val();
                const price = $(tableRows[i]).find('.cart_price').attr('data');
                newSubtotal += quantityValue * price;
            }

            $("#cart_total").append("$" + newSubtotal.toLocaleString());
        }
    }

    updateCartTotal();
});