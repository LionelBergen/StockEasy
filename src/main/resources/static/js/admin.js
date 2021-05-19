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


            // $('#categoryInputs')[0].innerHTML += innerHTMLToAdd;
            addToInnerHTML($('#categoryInputs')[0], innerHTMLToAdd);
        });
    }

    if ($('#addVariantFieldBtn')) {
        const innerHTMLToAdd = `<label>Description: </label><input type="text" name="variantName" /> <label>Price: </label> <input type="number" min="0.0" step="0.01" name="variantPrice" />`;
        $('#addVariantFieldBtn').on('click', function() {
            //$('#variantInputs')[0].innerHTML += innerHTMLToAdd;
            addToInnerHTML($('#variantInputs')[0], innerHTMLToAdd);
        });
    }

    // Simply using innerHTML += will reset the form, removing all user input and event listeners
    function addToInnerHTML(elementToAddTo, htmlToAddAsString) {
        let newcontent = document.createElement('span');
        newcontent.innerHTML = htmlToAddAsString;

        while (newcontent.firstChild) {
            elementToAddTo.appendChild(newcontent.firstChild);
        }
    }
});