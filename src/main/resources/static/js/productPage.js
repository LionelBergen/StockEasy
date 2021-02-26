$(document).ready(function() {
    const storeStack = [];
    let currentViewedProducts;
    const DEFAULT_QUANTITY = 1;
    const completeJSONData = JSON.parse($("#store_container_complete").text());

    populateTableFromData(completeJSONData);

    $("#product_back").on('click', function() {
        if ($("#product_search").val() != "") {
            $("#product_search").val("");
        }

        $("#store_table tbody").show();
        $("#product_table").hide();
        clearMessages();

        const lastResult = storeStack.pop();

        if (storeStack.length == 0) {
            $("#product_search").show();
        }

        backButton(lastResult, completeJSONData);
    });

    $("#product_search").on("keyup", function() {
       if(this.value.length == 0) {
           $("#product_back").click();
       } else {
           if (storeStack.length == 0) {
               storeStack.push(completeJSONData);
           }

           searchProducts(this.value);
       }
    });

    function backButton(newRow, allRows) {
        populateTableFromData(newRow, false);
    }

    function getHtmlForListing(listOfDataRows) {
        let innerHTMLTableData = "";

        if (listOfDataRows[0].type == "PRODUCT") {
            const productIds = listOfDataRows.map(value => value.id);
            getProductsByIds(storeStack.pop(), productIds);
        } else {
            for (let i=0; i<listOfDataRows.length; i++) {
                let classNames = 'list-group-item list-group-item-action';

                if (listOfDataRows[i].name == 'Vendors') {
                    classNames += " vendors_category ";
                }

                innerHTMLTableData += "<tr><td class='" + classNames + "'>" + listOfDataRows[i].name + "</td></tr>";
            }
        }

        return innerHTMLTableData;
    }

    function populateTableFromData(listOfDataRows, override) {
        let innerHTMLTableData = "";

        // if this is an Item we need to take the children of
        if (listOfDataRows.length == 1 && listOfDataRows[0].children && override !== false) {
            listOfDataRows = listOfDataRows[0].children;
        }

        innerHTMLTableData += getHtmlForListing(listOfDataRows);

        $("#store_table tbody").empty();
        $("#store_table tbody").append(innerHTMLTableData);

        $("#store_table td").unbind('click');
        $("#store_table td").on('click', function(){

            const selectionValue = this.innerText;
            const newData = listOfDataRows.filter(dataRow => dataRow.name == selectionValue);

            if (newData[0].children.length == 0) {
                const typeOfRow = newData[0].type;
                if (typeOfRow == 'CATEGORY') {
                    getProductsByCategoryId(listOfDataRows, newData[0].id);
                } else if (typeOfRow == 'VENDORS') {
                    getProductsByVendorId(listOfDataRows, newData[0].id);
                } else {
                    console.error("Unknown type: " + typeOfRow);
                }
            } else {
                // add to stack
                storeStack.push(listOfDataRows);

                populateTableFromData(newData);

                $("#product_search").hide();
            }
        });

        if (storeStack.length == 0) {
            $("#product_back").hide();
        } else {
            $("#product_back").show();
        }
    }

    function getProductsByCategoryId(previousData, categoryId) {
        $.ajax({
            type : "GET",
            url : "/getProducts",
            data : {
                categoryId: categoryId
            },
            success: function(data){
                data = JSON.parse(data);
                if (!data.error && data.result) {
                    if (data.result.length > 0 ) {
                        storeStack.push(previousData);
                        switchToProductView(data);
                        currentViewedProducts = data.result;
                        $("#product_search").hide();
                    } else {
                        // Category has no products, do nothing
                    }
                } else {
                    console.log('something went wrong..');
                    console.log(data);
                }
            },
            error: function(data) {
                console.log("ERROR:");
                console.log(data);
            }
        });
    }

    function getProductsByVendorId(previousData, vendorId) {
        $.ajax({
            type : "GET",
            url : "/getProductsByVendor",
            data : {
                vendorId: vendorId
            },
            success: function(data){
                data = JSON.parse(data);
                if (!data.error && data.result) {
                    if (data.result.length > 0 ) {
                        storeStack.push(previousData);
                        switchToProductView(data);
                        currentViewedProducts = data.result;
                        $("#product_search").hide();
                    } else {
                        // Category has no products, do nothing
                    }
                } else {
                    console.log('something went wrong..');
                    console.log(data);
                }
            },
            error: function(data) {
                console.log("ERROR:");
                console.log(data);
            }
        });
    }

    function switchToProductView(productData) {
        $("#quantity").val(DEFAULT_QUANTITY);
        $("#store_table tbody").hide();

        $("#product_back").show();
        $("#product_table").show();
        $("#product_table tbody").show();

        $("#product").empty();
        $("#variants_input_wrapper").empty();

        if (productData.result) {
            productData = productData.result;
        }

        $("#product_table tbody").empty();
        for (let i=0; i<productData.length; i++) {
            let tableRow = "";

            tableRow += "<tr><td class='product_name_td' colspan='3'>" + productData[i].productName + "</td></tr>";
            tableRow += "<tr>";
            tableRow += "<td class=\"price\">$" + productData[i].variants[0].price.toFixed(2) + "</td>";
            tableRow += "<td class='quantity_td'><input class=\"quantity\" type=\"number\" min=\"1\" value=\"1\"/></td>";
            tableRow += "<td><select class=\"variant_input\"> ";

            for (let j = 0; j < productData[i].variants.length; j++) {
                tableRow += "<option data-price='" + productData[i].variants[j].price + "' value='" + productData[i].variants[j].id + "'>" + productData[i].variants[j].name + "</option>";
            }

            tableRow += "</select></td>";
            tableRow += "<td><i row_id='" + i + "' class=\"fa fa-plus-circle addToCartButton\"></i></td></tr>";
            tableRow += "<tr></tr>";

            $("#product_table tbody").append(tableRow);
        }

        $(".addToCartButton").on("click", function() {
            const rowId = $(this).attr("row_id");
            // Get the row id. There are 3 rows for every product, and we want the 2nd row of those 3 for this product
            const realRowId = rowId == 0 ? 1 : ($(this).attr("row_id") * 3) + 1;
            const tableDataColumns = $($("#product_table tbody tr")[rowId]).find("td");

            const productId = currentViewedProducts[rowId].id;
            const variantId = $($("#product_table tbody tr")[realRowId]).find(".variant_input").val();
            const quantity = $($("#product_table tbody tr")[realRowId]).find(".quantity").val();

            addItemToCart(productId, variantId, quantity);

            // reset quantity back to 1
            $($("#product_table tbody tr")[realRowId]).find(".quantity").val("1");
        });

        $(".variant_input").on("change", function() {
             const newPrice = parseFloat($($(this).find(":selected")).attr('data-price'));
             const dataRow = $($($(this).parent()).parent());

            $(dataRow.find(".price")).text("$" + newPrice.toFixed(2));
        });
    }

    function clearMessages() {
        $("#messages_text").empty();
    }

    function setMessagesText(text) {
        clearMessages();

        console.log("appending text: ");
        $("#messages_text").append(text.id);
    }

    function addItemToCart(productId, variantId, quantity) {
        $.ajax({
            type : "GET",
            url : "/addProductToCart",
            data :   {
                productId: productId,
                variantId: variantId,
                quantity: quantity
            },
            success: function(data){
                data = JSON.parse(data);
                if (!data.error && data.result) {
                    showSnackBar();
                    const result = data.result;

                    if (result.productAmountChanged === true) {
                        const currentDataCount = parseInt($("#shopping-cart").attr("data-count"));
                        $("#shopping-cart").attr("data-count", currentDataCount + 1);
                    }
                } else {
                    console.error('something went wrong..');
                    console.error(data);
                }
            },
            error: function(error) {
                console.error('ERROR');
                console.error(error);
            }
        });
    }

    function searchProducts(searchString) {
        $.ajax({
            type : "GET",
            url : "/searchProducts",
            data :   {searchString : searchString },
            success: function(data){
                data = JSON.parse(data);
                console.log(data);
                switchToProductView(data);
            },
            error: function(error) {
                console.error('ERROR');
                console.error(error);
            }
        });
    }

    function showSnackBar() {
        var snakBarElement = document.getElementById("snackbar");

        // Add the "show" class to DIV
        snakBarElement.className = "show";

        // After 3 seconds, remove the show class from DIV
        setTimeout(function(){ snakBarElement.className = snakBarElement.className.replace("show", ""); }, 3000);
    }
});