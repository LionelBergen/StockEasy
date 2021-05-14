$(document).ready(function() {
    if($('#manage-stores')) {
        $("#manage-stores").on('click', function(){
            window.location.href = '/admin/manageStores';
        });
    }

    if($('#go-home')) {
        $("#go-home").on('click', function(){
            window.location.href = '/';
        });
    }
});