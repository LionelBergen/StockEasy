$(document).ready(function() {
    if($('#add-new-store')) {
        $("#add-new-store").on('click', function(){
            window.location.href = '/admin/add-new-store';
        });
    }
});