$(document).ready(function() {
    if($('#manage-stores')) {
        $("#manage-stores").on('click', function(){
            window.location.href = '/admin/manageStores';
        });
    }

    if($('#manage-vendors')) {
        $("#manage-vendors").on('click', function(){
            window.location.href = '/admin/manageVendors';
        });
    }

    if($('#manage-products')) {
        $("#manage-products").on('click', function(){
            window.location.href = '/admin/manageProducts';
        });
    }

    if($('#go-home')) {
        $("#go-home").on('click', function(){
            window.location.href = '/';
        });
    }
});