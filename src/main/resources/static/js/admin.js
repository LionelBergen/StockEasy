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


    if ($('#addCategoryFieldBtn')) {
        $("#addCategoryFieldBtn").on('click', function(){
            var innerHTMLToAdd = `<select name='productCategory' type='text'>`;

            for(let i=0; i<categories.length; i++) {
                innerHTMLToAdd += `<option value='${categories[i].id}'>${categories[i].name}</option>`;
            }
            innerHTMLToAdd += "</select>";


            $('#categoryInputs')[0].innerHTML += innerHTMLToAdd;
        });
    }

    if ($('#addVariantFieldBtn')) {
        const innerHTMLToAdd = `<label>Description: </label><input type="text" name="variantName" /> <label>Price: </label> <input type="number" min="0.0" name="variantPrice" />`;
        $('#addVariantFieldBtn').on('click', function() {
            $('#variantInputs')[0].innerHTML += innerHTMLToAdd;
        });
    }
});