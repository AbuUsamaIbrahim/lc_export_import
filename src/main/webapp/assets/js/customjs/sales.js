var rootBank = basicUrl + '/api/v1/banks';
var rootCust = basicUrl + '/api/v1/customers';
var rootGoods = basicUrl + '/api/v1/goods';
var rootSales = basicUrl + '/api/v1/sales';
var selectedUserId = 0;
var user_info = JSON.parse(window.localStorage.getItem('user_info'));
var dt;
var totalAmount = 0;
var bankId = 0;
var custId = 0;
var isGoods = 0;
var isBranch = 0;
var goodsId = 0;
$(document).ready(function () {
    $('#afterUpdateBtn').hide();
    $('#goodQuantity').keyup(calculate);
    $('#goodsUnitPrice').keyup(calculate);
});

function hideBtn() {
    $('#afterUpdateBtn').show();
    $('#mainBtn').hide();
}

function calculate(e) {
    var num = $('#goodQuantity').val() * $('#goodsUnitPrice').val();
    var val = new BigNumber(num);
    $('#goodsAmount').val(val.toFixed(2));
}

function calculateTotal(id, value) {
    var regex = /(?<=_)[a-z,0-9]+/;
    var st = regex.exec(id);
    var quantity = $("#goodsQuantity_" + st[0]).val();
    var unitPrice = $("#goodsUnitPrice_" + st[0]).val();
    var num = quantity * unitPrice;
    var val = new BigNumber(num);
    $("#goodsAmount_" + st).val(val.toFixed(2));
}

function getBranchByBank(bankName) {
    var api = rootBank + '/name?name=' + bankName;
    var newBankId;
    var advisingBank = [];
    $.ajax({
        type: "GET",
        url: api,
        async: false,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {

            // var selectOption = new Option("Select Bank", "", false, false);
            // $('#bankName').append(selectOption);
            if (res.status == "OK") {
                newBankId = res.content.id;

                api = rootBranch + '/allByBank?bankId=' + newBankId;
                $.ajax({
                    type: "GET",
                    url: api,
                    async: false,
                    headers: {'Authorization': 'Bearer ' + user_info.token},
                    contentType: 'application/json',
                    error: function (res) {
                        console.log(JSON.stringify(res));
                        alert(JSON.stringify(res));
                    },
                    success: function (res) {
                        var len = res.content.length;
                        // var selectOption = new Option("Select Bank", "", false, false);
                        // $('#bankName').append(selectOption);
                        for (var i = 0; i < len; i++) {
                            advisingBank.push(res.content[i].name);
                        }
                    }
                });
                advisingBank = advisingBank.map(function (x) {
                    return x.toUpperCase()
                });
                advisingBank.sort();

            }
        }
    });
    $("#advisingBranch").autocomplete({
        source: function (request, response) {
            var matches = $.map(advisingBank, function (advisingBank) {
                if (advisingBank.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                    return advisingBank;
                }
            });
            response(matches);
        }
    });
    $("#lcBranch").autocomplete({
        source: function (request, response) {
            var matches = $.map(advisingBank, function (advisingBank) {
                if (advisingBank.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                    return advisingBank;
                }
            });
            response(matches);
        }
    });
}

// function goodsDate(id, att) {
//     if (user_info == null) {
//         window.location = 'index.html';
//     }
//     $('#pUser').html(user_info.name);
//     var attri = att;
//     var api = rootGoods + '/' + parseInt(id);
//     $.ajax({
//         type: "GET",
//         url: api,
//         headers: {'Authorization': 'Bearer ' + user_info.token},
//         contentType: 'application/json',
//         error: function (res) {
//             console.log(JSON.stringify(res));
//             alert(JSON.stringify(res));
//         },
//         success: function (res) {
//
//             var len = res.content.length;
//
//             // $("#id").val( goods.id);
//             var price = res.content.unitPrice;
//             var quantity = res.content.quantity;
//             var total = price * quantity;
//             if (attri == "goodsDesc") {
//                 var preTotal = $("#goodsAmount").val();
//                 var preGrandTotal = $("#goodsTotalAmount").val();
//                 $('#goodsId').val(res.content.id);
//                 $("#goodQuantity").val(res.content.quantity);
//                 $("#goodsUnitPrice").val(res.content.unitPrice);
//                 $("#goodsUnit").val(res.content.unit);
//                 $("#goodPackOrMarks").val(res.content.packOrMarks);
//                 $("#goodsRemarks").val(res.content.remarks);
//                 $("#goodsAmount").val(total);
//                 if (totalAmount != 0) {
//                     totalAmount = preGrandTotal - preTotal;
//                 }
//                 totalAmount = totalAmount + total;
//                 $("#goodsTotalAmount").val(totalAmount);
//             } else {
//                 var len = attri.length;
//                 var id1 = attri.substr(12, len);
//                 var preTotal = $("#goodsAmount_id" + id1).val();
//                 var preGrandTotal = $("#goodsTotalAmount").val();
//                 $('#goodsId_id' + id1).val(res.content.id);
//                 $("#goodsQuantity_id" + id1).val(res.content.quantity);
//                 $("#goodsUnitPrice_id" + id1).val(res.content.unitPrice);
//                 $("#goodsUnit_id" + id1).val(res.content.unit);
//                 $("#goodPackOrMarks_id" + id1).val(res.content.packOrMarks);
//                 $("#goodsRemarks_id" + id1).val(res.content.remarks);
//                 $("#goodsAmount_id" + id1).val(total);
//                 var total_element = $(".element").length;
//                 if (totalAmount != 0) {
//                     totalAmount = preGrandTotal - preTotal;
//                 }
//                 totalAmount = totalAmount + total;
//                 $("#goodsTotalAmount").val(totalAmount);
//             }
//
//
//         }
//     });
// }

function getCustById(id) {
    if (user_info == null) {
        window.location = 'index.html';
    }

    $('#pUser').html(user_info.name);

    var api = rootCust + '/' + id;
    var custName = [];
    $.ajax({
        type: "GET",
        url: api,
        async: false,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {
            $('#purchaserAddress').val(res.content.address);
        }
    });
}

$(function () {
        if (user_info == null) {
            window.location = 'index.html';
        }
        $('#pUser').html(user_info.name);

        var api = rootSales + '/all';
        var performa = [];
        $.ajax({
            type: "GET",
            url: api,
            headers: {'Authorization': 'Bearer ' + user_info.token},
            contentType: 'application/json',
            error: function (res) {
                console.log(JSON.stringify(res));
                alert(JSON.stringify(res));
            },
            success: function (res) {
                var selectOption = new Option("Select Customer", "", false, false);
                $('#purchaserName').append(selectOption);
                var len = res.content.length;

                for (var i = 0; i < len; i++) {
                    if (res.content[i].proformaInvoiceNo != null) {
                        performa.push(res.content[i].proformaInvoiceNo);
                    }

                    // var options = new Option(res.content[i].name, res.content[i].id, false, false);
                    // $('#purchaserName').append(options);
                }
                performa = performa.map(function (x) {
                    return x.toUpperCase()
                });
                performa.sort();
            }
        });
        $("#proformaInvoiceNo").autocomplete({
            source: function (request, response) {
                var matches = $.map(performa, function (performa) {
                    if (performa.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                        return performa;
                    }
                });
                response(matches);
            }
        });
    }
);

$(function () {
        if (user_info == null) {
            window.location = 'index.html';
        }
        $('#pUser').html(user_info.name);

        var api = rootCust + '/all';
        var custName = [];
        $.ajax({
            type: "GET",
            url: api,
            headers: {'Authorization': 'Bearer ' + user_info.token},
            contentType: 'application/json',
            error: function (res) {
                console.log(JSON.stringify(res));
                alert(JSON.stringify(res));
            },
            success: function (res) {
                var selectOption = new Option("Select Customer", "", false, false);
                $('#purchaserName').append(selectOption);
                var len = res.content.length;

                for (var i = 0; i < len; i++) {
                    if (res.content[i].name != null) {
                        custName.push(res.content[i].name);
                    }

                    // var options = new Option(res.content[i].name, res.content[i].id, false, false);
                    // $('#purchaserName').append(options);
                }
                custName = custName.map(function (x) {
                    return x.toUpperCase()
                });
                custName.sort();
            }
        });
        $("#purchaserName").autocomplete({
            source: function (request, response) {
                var matches = $.map(custName, function (custName) {
                    if (custName.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                        return custName;
                    }
                });
                response(matches);
            }
        });
    }
);
//
// function custDropDown() {
//
//     if (user_info == null) {
//         window.location = 'index.html';
//     }
//     $('#pUser').html(user_info.name);
//
//     var api = rootCust + '/all';
//     var custName=[];
//     $.ajax({
//         type: "GET",
//         url: api,
//         headers: {'Authorization': 'Bearer ' + user_info.token},
//         contentType: 'application/json',
//         error: function (res) {
//             console.log(JSON.stringify(res));
//             alert(JSON.stringify(res));
//         },
//         success: function (res) {
//             var selectOption = new Option("Select Customer", "", false, false);
//             $('#purchaserName').append(selectOption);
//             var len = res.content.length;
//
//             for (var i = 0; i < len; i++) {
//                 custName.push(res.content[i].name);
//                 // var options = new Option(res.content[i].name, res.content[i].id, false, false);
//                 // $('#purchaserName').append(options);
//             }
//         }
//     });
//     $( "#purchaserName" ).autocomplete({
//         source: custName
//     });
// }
// $( function() {
//         if (user_info == null) {
//             window.location = 'index.html';
//         }
//         $('#pUser').html(user_info.name);
//
//         var api = rootBranch + '/all';
//         var advisingBank=[];
//         $.ajax({
//             type: "GET",
//             url: api,
//             headers: {'Authorization': 'Bearer ' + user_info.token},
//             contentType: 'application/json',
//             error: function (res) {
//                 console.log(JSON.stringify(res));
//                 alert(JSON.stringify(res));
//             },
//             success: function (res) {
//
//                 var len = res.content.length;
//
//                 for (var i = 0; i < len; i++) {
//                     if(res.content[i].name!=null){
//                         advisingBank.push(res.content[i].name);
//                     }
//
//                 }
//                 advisingBank = advisingBank.map(function(x){ return x.toUpperCase() });
//                 advisingBank.sort();
//             }
//         });
//         $( "#advisingBranch" ).autocomplete({
//             source: function( request, response ) {
//                 var matches = $.map( advisingBank, function(advisingBank) {
//                     if ( advisingBank.toUpperCase().indexOf(request.term.toUpperCase()) === 0 ) {
//                         return advisingBank;
//                     }
//                 });
//                 response(matches);
//             }
//         });
//         $("#lcBranch").autocomplete({
//             source: function( request, response ) {
//                 var matches = $.map( advisingBank, function(advisingBank) {
//                     if ( advisingBank.toUpperCase().indexOf(request.term.toUpperCase()) === 0 ) {
//                         return advisingBank;
//                     }
//                 });
//                 response(matches);
//             }
//         });
//     }
// );

$(function () {
        if (user_info == null) {
            window.location = 'index.html';
        }
        $('#pUser').html(user_info.name);

        var api = rootBank + '/all';
        var advisingBank = [];
        $.ajax({
            type: "GET",
            url: api,
            headers: {'Authorization': 'Bearer ' + user_info.token},
            contentType: 'application/json',
            error: function (res) {
                console.log(JSON.stringify(res));
                alert(JSON.stringify(res));
            },
            success: function (res) {

                var len = res.content.length;

                for (var i = 0; i < len; i++) {
                    if (res.content[i].name != null) {
                        advisingBank.push(res.content[i].name);
                    }

                }
                advisingBank = advisingBank.map(function (x) {
                    return x.toUpperCase()
                });
                advisingBank.sort();
            }
        });
        $("#bankName").autocomplete({
            source: function (request, response) {
                var matches = $.map(advisingBank, function (advisingBank) {
                    if (advisingBank.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                        return advisingBank;
                    }
                });
                response(matches);
            }
        });
        $("#lcBankName").autocomplete({
            source: function (request, response) {
                var matches = $.map(advisingBank, function (advisingBank) {
                    if (advisingBank.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                        return advisingBank;
                    }
                });
                response(matches);
            }
        });
    }
);

$(function () {
        if (user_info == null) {
            window.location = 'index.html';
        }
        $('#pUser').html(user_info.name);

        var api = rootGoods + '/all';
        var goods = [];
        $.ajax({
            type: "GET",
            url: api,
            headers: {'Authorization': 'Bearer ' + user_info.token},
            contentType: 'application/json',
            error: function (res) {
                console.log(JSON.stringify(res));
                alert(JSON.stringify(res));
            },
            success: function (res) {

                var len = res.content.length;

                for (var i = 0; i < len; i++) {
                    if (res.content[i].description != null) {
                        goods.push(res.content[i].description);
                    }

                }
                goods = goods.map(function (x) {
                    return x.toUpperCase()
                });
                goods.sort();
            }
        });
        $("#goodsDesc").autocomplete({
            source: function (request, response) {
                var matches = $.map(goods, function (goods) {
                    if (goods.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                        return goods;
                    }
                });
                response(matches);
            }
        });

    }
);


$(document).on("click", ".deleteIcon", function () {
    var tv = $('#goodsTotalAmount').val();
    var indexDelAgent = $(this).closest('tr').index('#myTable tr');
    var goodsTotalAmountToDelete = $('#myTable tr').find('td:nth-child(6)').map(function () {
        return $(this).text()
    }).get();
    for (i = 1; i < goodsTotalAmountToDelete.length; i++) {
        if (i === indexDelAgent) {
            if (tv <= 0) {
                break;
            }
            totalAmount = Number(tv) - Number(goodsTotalAmountToDelete[Number(indexDelAgent)]);
        }

    }

    console.log("after deletion");
    console.log(indexDelAgent);

    document.getElementById("myTable").deleteRow(indexDelAgent);
    var val = new BigNumber(totalAmount);
    $('#goodsTotalAmount').val(val.toFixed(2));
});

//Compose template string
String.prototype.compose = (function () {
    var re = /\{{(.+?)\}}/g;
    return function (o) {
        return this.replace(re, function (_, k) {
            return typeof o[k] != 'undefined' ? o[k] : '';
        });
    }
}());


$('#tableBtn').click(function () {
    var i = 0;

    // var rowCount = $('table#myTable tr:last').index() + 1;
    var rowCount = Math.floor((Math.random() * 10000) + 1);

    var tbody = $('#myTable').children('tbody');
    var table = tbody.length ? tbody : $('#myTable');
    var row = '<tr id="row_' + rowCount + '">' +
        '<td style="display: none">{{id}}</td>' +
        '<td id="goodDesc_' + rowCount + '">{{description}}</td>' +
        '<td id="goodQuantity_' + rowCount + '">{{quantity}}</td>' +
        '<td id="goodUnitPrice_' + rowCount + '">{{unitPrice}}</td>' +
        '<td id="goodUnit_' + rowCount + '">{{unit}}</td>' +
        '<td id="goodAmount_' + rowCount + '">{{totalAmount}}</td>' +
        '<td id="goodPackMarks_' + rowCount + '">{{packMarks}}</td>' +
        '<td><i value="Delete" type="button" alt="Delete6" class="deleteIcon fa fa-trash"></i></button></td>'
    '</tr>';
    //Add row
    table.append(row.compose({
        'id': rowCount,
        'description': $('#goodsDesc').val(),
        'quantity': $('#goodQuantity').val(),
        'unitPrice': $('#goodsUnitPrice').val(),
        'unit': $('#goodsUnit').val(),
        'totalAmount': $('#goodsAmount').val(),
        'packMarks': $('#goodPackOrMarks').val(),

    }));
    totalAmount = 0;
    var goodsTotalAmount = $('#myTable tr').find('td:nth-child(6)').map(function () {
        return $(this).text()
    }).get();
    for (i = 1; i < goodsTotalAmount.length; i++) {
        totalAmount = totalAmount + Number(goodsTotalAmount[i]);
    }

    $('#goodsTotalAmount').val(totalAmount);

});

function customerCheck(custName, address) {

    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);

    var api = rootCust + '/name?name=' + encodeURIComponent(custName) + '&address=' + encodeURIComponent(address);


    $.ajax({
        type: "GET",
        url: api,
        async: false,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {

            // var selectOption = new Option("Select Bank", "", false, false);
            // $('#bankName').append(selectOption);
            if (res.status == "OK") {
                custId = res.content.id;
            } else {
                var payload = {name: custName, address: address, mobileNo: "01000000000"};
                var api = rootCust + '/create';

                $.ajax({
                    type: "POST",
                    url: api,
                    async: false,
                    data: JSON.stringify(payload),
                    headers: {'Authorization': 'Bearer ' + user_info.token},
                    contentType: 'application/json',
                    error: function (res2) {
                        alert(JSON.stringify(res2));
                    },
                    success: function (res2) {
                        if (res2.status == "Created") {
                            custId = res2.savedId;
                            // alert("Customer Saved");
                        }

                    }

                });

            }
            // var len = res.content.length;
            // for (var i = 0; i < len; i++) {
            //     // var options = new Option(res.content[i].bankBranchName, res.content[i].id, false, false);
            //     // $('#bankName').append(options);
            // }
        }
    });

}

function goodsCheck(quantity, unitPrice, unit, packOrMarks, desc) {

    var api = "";
    var name = desc.trim();
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    api = rootGoods + '/name?desc=' + encodeURIComponent(name);
    $.ajax({
        type: "GET",
        url: api,
        async: false,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {

            // var selectOption = new Option("Select Bank", "", false, false);
            // $('#bankName').append(selectOption);
            if (res.status == "OK") {
                goodsId = res.content.id;
            } else {
                var payload = {
                    quantity: quantity,
                    unitPrice: unitPrice,
                    unit: unit,
                    packOrMarks: packOrMarks,
                    description: desc,
                    remarks: ""
                };
                var api = rootGoods + '/create';

                $.ajax({
                    type: "POST",
                    url: api,
                    async: false,
                    data: JSON.stringify(payload),
                    headers: {'Authorization': 'Bearer ' + user_info.token},
                    contentType: 'application/json',
                    error: function (res2) {
                        alert(JSON.stringify(res2));
                    },
                    success: function (res2) {
                        goodsId = res2.savedId;
                        // alert("Goods Saved");
                    }

                });
            }
            // var len = res.content.length;
            // for (var i = 0; i < len; i++) {
            //     // var options = new Option(res.content[i].bankBranchName, res.content[i].id, false, false);
            //     // $('#bankName').append(options);
            // }
        }
    });

}

function branchCheck(branchName, bankId) {
    var branch = branchName.trim();
    var api = "";
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    api = rootBranch + '/name?name=' + encodeURIComponent(branch) + "&bankId=" + encodeURIComponent(bankId);
    $.ajax({
        type: "GET",
        url: api,
        async: false,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {

            // var selectOption = new Option("Select Bank", "", false, false);
            // $('#bankName').append(selectOption);
            if (res.status == "OK") {
                isBranch = 1;
            } else {
                var name = branch;
                var payload = {name: name, address: "", bank: {id: bankId}};
                var api = rootBranch + '/create';

                $.ajax({
                    type: "POST",
                    url: api,
                    async: false,
                    data: JSON.stringify(payload),
                    headers: {'Authorization': 'Bearer ' + user_info.token},
                    contentType: 'application/json',
                    error: function (res2) {
                        alert(JSON.stringify(res2));
                    },
                    success: function (res2) {
                        // bankId=res.savedId;
                        // alert("branch Saved");
                    }

                });
            }
            // var len = res.content.length;
            // for (var i = 0; i < len; i++) {
            //     // var options = new Option(res.content[i].bankBranchName, res.content[i].id, false, false);
            //     // $('#bankName').append(options);
            // }
        }
    });

}

// function bankBranchDropDown() {
//
//     if (user_info == null) {
//         window.location = 'index.html';
//     }
//     $('#pUser').html(user_info.name);
//
//     var api = rootBank + '/allBankBranch';
//     $.ajax({
//         type: "GET",
//         url: api,
//         headers: {'Authorization': 'Bearer ' + user_info.token},
//         contentType: 'application/json',
//         error: function (res) {
//             console.log(JSON.stringify(res));
//             alert(JSON.stringify(res));
//         },
//         success: function (res) {
//             var selectOption = new Option("Select LC Bank", "", false, false);
//             $('#lcBankName').append(selectOption);
//             var len = res.content.length;
//             for (var i = 0; i < len; i++) {
//                 var options = new Option(res.content[i].bankBranchName, res.content[i].id, false, false);
//                 $('#lcBankName').append(options);
//             }
//         }
//     });
// }
//
function bankDropDown(bankName, bankType) {
    // var regExpBankName=/^[^\(,]*/;
    // var lcBankName= regExpBankName.exec(bankName);
    // var AdvisingBankName = regExpBankName.exec(bankName);
    var api = "";
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    if (bankType == "advising") {
        api = rootBank + '/name?name=' + encodeURIComponent(bankName);
    }
    if (bankType == "lc") {
        api = rootBank + '/name?name=' + encodeURIComponent(bankName);
    }

    $.ajax({
        type: "GET",
        url: api,
        async: false,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {

            // var selectOption = new Option("Select Bank", "", false, false);
            // $('#bankName').append(selectOption);
            if (res.status == "OK") {
                bankId = res.content.id;
            } else {
                var name = "";
                if (bankType == "advising") {
                    name = bankName;
                }
                if (bankType == "lc") {
                    name = bankName;
                }

                var payload = {name: name};
                var api = rootBank + '/create';

                $.ajax({
                    type: "POST",
                    url: api,
                    async: false,
                    data: JSON.stringify(payload),
                    headers: {'Authorization': 'Bearer ' + user_info.token},
                    contentType: 'application/json',
                    error: function (res2) {
                        alert(JSON.stringify(res2));
                    },
                    success: function (res2) {
                        bankId = res2.savedId;
                        // alert("bank Saved");
                    }

                });
            }
            // var len = res.content.length;
            // for (var i = 0; i < len; i++) {
            //     // var options = new Option(res.content[i].bankBranchName, res.content[i].id, false, false);
            //     // $('#bankName').append(options);
            // }
        }
    });

}

function goodsDropDown() {

    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);

    var api = rootGoods + '/all';
    $.ajax({
        type: "GET",
        url: api,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {

            var selectOption = new Option("Select Goods", "", false, false);
            $('#goodsDesc').append(selectOption);
            var len = res.content.length;
            for (var i = 0; i < len; i++) {
                var options = new Option(res.content[i].description, res.content[i].id, false, false);
                $('#goodsDesc').append(options);
            }
        }
    });
}

$(document).ready(function () {
    // $('.select2').select2();
    // custDropDown();
    // bankDropDown();
    // bankBranchDropDown();

    getAllSales();
    $('.datepicker').datepicker();

    var i = 0;
    $(".add").click(function () {
        i++;
        var total_element = $(".element").length;
        // last <div> with element class id
        var lastid = $(".element:last").attr("id");
        var split_id = lastid.split("_");
        var nextindex = (split_id[1]) + i;
        var max = 5;
        // Check total number elements
        if (total_element < max) {
            // Adding new div container after last occurance of element class
            $(".element:last").after("<div class='element' id='div_" + nextindex + "'></div>");
            // Adding element to <div>
            $("#div_" + nextindex).append("<br>" +
                "<div class=\"border border-secondary\">" +
                "<br>" +
                "<div id='txt_" + nextindex + "'>" +
                "<div class=\"form-group row\">" +
                "<label for=\"goodsDesc\" class=\"col-sm-2 col-form-label\">Goods Desc.</label>" +
                "<div class=\"col-sm-8\"><input  class=\"form-control select2\" name='goodsDesc_" + nextindex + "' id='goodsDesc_" + nextindex + "'/></div>" +
                " <div class=\"col-sm-2\">\n" +
                "  <button id='remove_" + nextindex + "' class='remove' type=\"button\" onclick=\"removeTotal(this.id)\"\n" +
                " ><i class=\"fa fa-close\"></i>\n" +
                "</button>\n" +
                "</div>" +
                "</div>" +
                " <div class=\"form-group row\">\n" +
                " <input name='goodsId_" + nextindex + "' type=\"hidden\" id='goodsId_" + nextindex + "'>" +
                "                                                            <label for=\"goodQuantity\"\n" +
                "                                                                   class=\"col-sm-2 col-form-label\">Quantity</label>\n" +
                "                                                            <div class=\"col-sm-2\">\n" +
                "                                                                <input type=\"text\" class=\"form-control\"\n" +
                "                                              onkeyup=\"calculateTotal(this.id,this.value)\" name='goodsQuantity_" + nextindex + "' id='goodsQuantity_" + nextindex + "'>\n" +
                "                                                            </div>\n" +
                "                                                            <label for=\"goodsUnitPrice\"\n" +
                "                                                                   class=\"col-sm-2 col-form-label\">Unit\n" +
                "                                                                Price</label>\n" +
                "                                                            <div class=\"col-sm-2\">\n" +
                "                                                                <input type=\"text\" class=\"form-control\"\n" +
                "                                                                     onkeyup=\"calculateTotal(this.id,this.value)\" id='goodsUnitPrice_" + nextindex + "' name='goodsUnitPrice_" + nextindex + "'>\n" +
                "                                                            </div>\n" +
                "                                                            <label for=\"goodsUnit\"\n" +
                "                                                                   class=\"col-sm-2 col-form-label\">Unit</label>\n" +
                "                                                            <div class=\"col-sm-2\">\n" +
                "                                                                <input type=\"text\" class=\"form-control\"\n" +
                "                                                                       id='goodsUnit_" + nextindex + "' name='goodsUnit_" + nextindex + "'>\n" +
                "                                                            </div>\n" +
                "                                                        </div>\n" +
                "                                                        <div class=\"form-group row\">\n" +
                "                                                            <label for=\"goodPackOrMarks\"\n" +
                "                                                                   class=\"col-sm-2 col-form-label\">Pack\n" +
                "                                                                / Marks</label>\n" +
                "                                                            <div class=\"col-sm-2\">\n" +
                "                                                                <input type=\"text\" class=\"form-control\"\n" +
                "                                                                       id='goodPackOrMarks_" + nextindex + "' name='goodPackOrMarks_" + nextindex + "'>\n" +
                "                                                            </div>\n" +

                "                                                            <label for=\"goodsAmount\"\n" +
                "                                                                   class=\"col-sm-2 col-form-label\">Total\n" +
                "                                                                Amount</label>\n" +
                "                                                            <div class=\"col-sm-2\">\n" +
                "                                                                <input type=\"text\" class=\"form-control\"\n" +
                "                                                                        id='goodsAmount_" + nextindex + "'>\n" +
                "                                                            </div>\n" +
                "                                                        </div>" +

                "</div>" +
                "</div>" +
                "<br>");

        }

        var des = $('#goodsDesc').val();
        // sectionDropdown(des, nextindex);

        var id = $('#goodsId').val();
        var q = $('#goodQuantity').val();
        var up = $('#goodsUnitPrice').val();
        var u = $('#goodsUnit').val();
        var pom = $('#goodPackOrMarks').val();
        var total = $("#goodsAmount").val();
        // totalAmount = Number(totalAmount) + Number(total);

        $('#goodsDesc_' + nextindex).val(des);
        $('#goodsId').val("");
        $('#goodQuantity').val("");
        $('#goodsUnitPrice').val("");
        $('#goodsUnit').val("");
        $('#goodPackOrMarks').val("");
        $("#goodsAmount").val("");

        $('#goodsId_' + nextindex).val(id);
        $('#goodsQuantity_' + nextindex).val(q);
        $('#goodsUnitPrice_' + nextindex).val(up);
        $('#goodsUnit_' + nextindex).val(u);
        $('#goodPackOrMarks_' + nextindex).val(pom);
        $('#goodsAmount_' + nextindex).val(total);
        // $("#goodsTotalAmount").val(totalAmount);
    });

    $('.container').on('click', '.remove', function () {

        var id = this.id;
        var split_id = id.split("_");
        var deleteindex = split_id[1];

        // Remove <div> with id
        $("#div_" + deleteindex).remove();

    });
});

function sectionDropdown(des, index) {
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    var des = des;
    var nextindex = index;
    var api = rootGoods + '/all';
    $.ajax({
        type: "GET",
        url: api,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            console.log(JSON.stringify(res));
            alert(JSON.stringify(res));
        },
        success: function (res) {


            var len = res.content.length;
            var options;
            for (var i = 0; i < len; i++) {
                if (res.content[i].id == des) {
                    options = new Option(res.content[i].description, res.content[i].id, false, true);
                    $('#goodsDesc_' + nextindex).append(options);
                    continue;
                }
                options = new Option(res.content[i].description, res.content[i].id, false, false);
                $('#goodsDesc_' + nextindex).append(options);


            }
        }
    });
}

function removeTotal(id) {
    var len = id.length;
    var st = id.substr(7, len);
    var num = $("#goodsAmount_" + st).val();
    // var totalAmount=$("#goodsTotalAmount").val();
    totalAmount = Number(totalAmount) - Number(num);
    $("#goodsTotalAmount").val(totalAmount);
}

// function getDatePicker(eId) {
//     $('#' + eId).daterangepicker({
//         singleDatePicker: true,
//         showDropdowns: true,
//         minYear: 2007,
//         maxYear: parseInt(moment().format('YYYY'), 10) + 10,
//         locale: {
//             format: 'DD/MM/YYYY'
//         }
//     });
//     $('#' + eId).val('');
// }

var rootBank = basicUrl + '/api/v1/banks';
var rootBranch = basicUrl + '/api/v1/branches';
var branchList = [];
var selectedUserId = 0;
var user_info = JSON.parse(window.localStorage.getItem('user_info'));
var dt;

function updateGoods(arr) {
    var remarks = "Product Updated from sales";
    if (arr[0] != "") {
        var api = basicUrl + '/api/v1/goods/' + arr[0];
        var payload = {
            id: arr[0],
            quantity: arr[1],
            unitPrice: arr[2],
            unit: arr[3],
            packOrMarks: arr[4],
            description: arr[5],
            remarks: remarks
        };

        $.ajax({
            type: "PUT",
            url: api,
            data: JSON.stringify(payload),
            headers: {'Authorization': 'Bearer ' + user_info.token},
            contentType: 'application/json',
            error: function (res) {
                alert(JSON.stringify(res));
            },
            success: function (res) {
                var msg = 'Goods Updated Successfully';
                alert(msg);
            }

        });
    }


}

function saveSales(val) {
    if (val == "createNew") {
        selectedUserId = 0
    }
    var tableColumnIdVal = $('#myTable tr').find('td:first').map(function () {
        return $(this).text()
    }).get();
    var goodsDesc = $('#myTable tr').find('td:nth-child(2)').map(function () {
        return $(this).text()
    }).get();
    var goodsQuantity = $('#myTable tr').find('td:nth-child(3)').map(function () {
        return $(this).text()
    }).get();
    var goodsUnitPrice = $('#myTable tr').find('td:nth-child(4)').map(function () {
        return $(this).text()
    }).get();
    var goodsUnit = $('#myTable tr').find('td:nth-child(5)').map(function () {
        return $(this).text()
    }).get();
    var totalAmount = $('#myTable tr').find('td:nth-child(6)').map(function () {
        return $(this).text()
    }).get();
    var packMarks = $('#myTable tr').find('td:nth-child(7)').map(function () {
        return $(this).text()
    }).get();

    var tableLength = $('#myTable tbody tr').length;
    var id, quantity, unitPrice, unit, packOrMarks, desc, price;
    var arr = [];
    var total_element = $(".element").length;
    for (i = 1; i < tableLength; i++) {

        quantity = goodsQuantity[i];
        unitPrice = goodsUnitPrice[i];
        unit = goodsUnit[i];
        packOrMarks = packMarks[i];
        desc = goodsDesc[i];
        price = totalAmount[i];
        goodsCheck(quantity, unitPrice, unit, packOrMarks, desc);
        var goods = {
            "id": goodsId,
            "quantity": Number(quantity),
            "unitPrice": Number(unitPrice),
            "unit": unit,
            "packOrMarks": Number(packOrMarks),
            "description": desc
        };

        arr.push(goods);
        // if (i == 0) {
        //     id = $("#goodsId").val();
        //     quantity = $("#goodQuantity").val();
        //     unitPrice = $("#goodsUnitPrice").val();
        //     unit = $("#goodsUnit").val();
        //     packOrMarks = $("#goodPackOrMarks").val();
        //     desc=$('#goodsDesc').val();
        //     var goods = {
        //         "id": id,
        //         "quantity": quantity,
        //         "unitPrice": unitPrice,
        //         "unit": unit,
        //         "packOrMarks": packOrMarks,
        //         "goodsDesc":desc
        //     };
        //
        //     arr.push(goods);
        //     //
        //     // arr.push(packOrMarks);
        //     // desc=$("#goodsDesc :selected").text();
        //     // arr.push(desc);
        //
        // } else {
        //     if (i - 1 != 0) {
        //         var s = i - 1;
        //         i = s.toString() + i;
        //     }
        //     id = $("#goodsId_id1" + i).val();
        //     quantity = $("#goodsQuantity_id1" + i).val();
        //     unitPrice = $("#goodsUnitPrice_id1" + i).val();
        //     unit = $("#goodsUnit_id1" + i).val();
        //     packOrMarks = $("#goodPackOrMarks_id1" + i).val();
        //     desc=$("#goodsDesc_id1" + i).val();
        //     var goods = {
        //         "id": id,
        //         "quantity": quantity,
        //         "unitPrice": unitPrice,
        //         "unit": unit,
        //         "packOrMarks": packOrMarks,
        //         "goodsDesc":desc
        //     };
        //
        //     arr.push(goods);
        //     //
        //     // arr.push(packOrMarks);
        //     // var des=document.getElementById("goodsDesc_id1"+i);
        //     // desc=des.options[des.selectedIndex].text;
        //     // arr.push(desc);
        //     // updateGoods(arr);
        // }
    }


    var $form = $("#form_data");
    // var data = getFormData($form);
    var data = ($("#form_date").serializeArray()); //  <-----------
    var indexed_array = {};
    // for (j = 0; j < data.length; j++) {
    //     if (data[j].value != "") {
    $.map(data, function (n, i) {

        indexed_array[n['name']] = n['value'];


    });
    //     }
    // }

    var totalAmount2 = $("#goodsTotalAmount").val();
    var totalAmount1 = new BigNumber(totalAmount2);
    var cust = $("#purchaserName").val();
    var address = $('#purchaserAddress').val();
    if (address != null || address != "") {
        customerCheck(cust, address);
    }

    var advisingBank = $("#bankName").val();
    // var advisingBankBranch=$("#bankName").find(":selected").text();
    // var lcBank = $("#lcBankName").find(":selected").text();
    var lcBank = $("#lcBankName").val();
    // var regExp = /\(([^)]+)\)/;
    var bankType1 = "advising";
    // var branchNameLc = regExp.exec(lcBank);
    // var branchNameAdv=regExp.exec(advisingBank);

    bankDropDown(advisingBank, bankType1);
    var advisingBankId = bankId;
    var branchName = $('#advisingBranch').val();
    if (branchName != null || branchName != "") {
        branchCheck(branchName, advisingBankId);
    }

    var bankType2 = "lc";
    bankDropDown(lcBank, bankType2);
    var lcBankId = bankId;
    var lcBranchF = $('#lcBranch').val();
    if (lcBranchF != null || lcBranchF != "") {
        branchCheck(lcBranchF, lcBankId);
    }

    var api = "";
    var idMain = "";
    if (selectedUserId == 0) {
        api = rootSales + '/create';
        idMain = "";
    } else {
        api = rootSales + '/' + selectedUserId;
        idMain = selectedUserId;
    }

    var payload = {
        id: idMain,
        invoiceNo: indexed_array.invoiceNo,
        proformaInvoiceNo: indexed_array.proformaInvoiceNo,
        lcDays: indexed_array.lcDays,
        lcOpenDays: indexed_array.lcOpenDays,
        paymentDays: indexed_array.paymentDays,
        shipmentWithinDays: indexed_array.shipmentWithinDays,
        lcNo: indexed_array.lcNo,
        challanNo: indexed_array.challanNo,
        exportLcNo: indexed_array.exportLcNo,
        county: indexed_array.county,
        loadingPlace: indexed_array.loadingPlace,
        partialShipment: indexed_array.partialShipment,
        placeOfDischarge: indexed_array.placeOfDischarge,
        truckChallanSerialNo: indexed_array.truckChallanSerialNo,
        truckChallanDate: (indexed_array.truckChallanDate),
        date: (indexed_array.date),
        vatNo: indexed_array.vatNo,
        finalDestination: indexed_array.finalDestination,
        shipmentMode: indexed_array.shipmentMode,
        tinNo: indexed_array.tinNo,
        truckNo: indexed_array.truckNo,
        termsOfPayment: indexed_array.termsOfPayment,
        piDate: (indexed_array.piDate),
        challanDate: (indexed_array.challanDate),
        totalAmount: totalAmount1.toFixed(2),
        lcDate: (indexed_array.lcDate),
        exchangeDate: (indexed_array.exchangeDate),
        customer: {id: custId},
        advisingBank: {id: advisingBankId},
        lcBank: {id: lcBankId},
        lcBranch: lcBranchF,
        advisingBranch: branchName,
        goodsList: arr
    };
    $.ajax({
        type: selectedUserId == 0 ? "POST" : "PUT",
        url: api,
        data: JSON.stringify(payload),
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            var msg = selectedUserId == 0 ? 'Sales Created Successfully' : 'Sales Updated Successfully';
            alert(msg);
            $("#exampleModal").modal('hide');
            window.setTimeout(function () {
                window.location.reload();
            }, 1000);
            // refreshForm();
        }

    });
    // return indexed_array;
    console.log(indexed_array);
    return false; //don't submit

}

function setInputFilter(textbox, inputFilter) {
    ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop"].forEach(function (event) {
        textbox.addEventListener(event, function () {
            if (inputFilter(this.value)) {
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;
            } else if (this.hasOwnProperty("oldValue")) {
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                this.value = "";
            }
        });
    });
}

setInputFilter(document.getElementById("goodsUnitPrice"), function (value) {
    return /^\d*\.?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
});
setInputFilter(document.getElementById("goodQuantity"), function (value) {
    return /^-?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
});
setInputFilter(document.getElementById("goodPackOrMarks"), function (value) {
    return /^-?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
});
// setInputFilter(document.getElementById("lcDays"), function (value) {
//     return /^-?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
// });
// setInputFilter(document.getElementById("lcOpenDays"), function (value) {
//     return /^-?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
// });
// setInputFilter(document.getElementById("paymentAtDays"), function (value) {
//     return /^-?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
// });
// setInputFilter(document.getElementById("shipmentWithinDays"), function (value) {
//     return /^-?\d*$/.test(value); // Allow digits and '.' only, using a RegExp
// });

function refreshForm() {
    // var selectOption = new Option("Select Customer", "", false, false);
    // $('#purchaserName').append(selectOption);
    $("#form_date")[0].reset();
    var rowCount = myTable.rows.length;
    for (var i = rowCount - 1; i > 0; i--) {
        myTable.deleteRow(i);
    }
    var idArray = [];

    $('.remove').each(function () {

        idArray.push(this.id);


    });
    var len = idArray.length;
    for (var i = 0; i < len; i++) {
        if (idArray[i] != "") {
            var id = idArray[i];
            var split_id = id.split("_");
            var deleteindex = split_id[1];
            $("#div_" + deleteindex).remove();
        }

    }
    // var id = this.id;
    // var split_id = id.split("_");
    // var deleteindex = split_id[1];
    //
    // // Remove <div> with id
    // $("#div_" + deleteindex).remove();

}

function removeSales(id) {


    var api = rootSales + '/' + id;
    if (window.confirm('Are you sure to delete user?')) {
        $.ajax({
            type: "DELETE",
            url: api,
            headers: {'Authorization': 'Bearer ' + user_info.token},
            contentType: 'application/json',
            error: function (res) {
                console.log(JSON.stringify(res));
                alert(JSON.stringify(res));
            },
            success: function (res) {
                alert('Sales Deleted Successfully');
                window.setTimeout(function () {
                    window.location.reload();
                }, 1000);
            }
        });
    }
}

// function updateSales(btn) {
//     var data = dt.row($(btn).parents('tr')).data();
//     var id = data[0];
//     selectedUserId = id;
//     var api = rootSales + '/' + id;
//     $.ajax({
//         type: "GET",
//         url: api,
//         headers: {'Authorization': 'Bearer ' + user_info.token},
//         contentType: 'application/json',
//         error: function (res) {
//             alert(JSON.stringify(res));
//         },
//         success: function (res) {
//
//             $("#proformaInvoiceNo").val(res.content.proformaInvoiceNo);
//             $("#piDate").val(res.content.piDate);
//             $("#purchaserName").val();
//             $("#bankName").val(res.content.advisingBank.id).change();
//             $("#lcBankName").val(res.content.lcBank.id).change();
//             $("#billOfExcDate").val(res.content.exchangeDate);
//             $("#lcDays").val(res.content.lcDays);
//             $("#lcNo").val(res.content.lcNo);
//             $("#expLcNo").val(res.content.exportLcNo);
//             $("#lcDate").val(res.content.lcDate);
//             $("#countryOrigin").val(res.content.county);
//             $("#placePfLanding").val(res.content.loadingPlace);
//             $("#partialShipment").val(res.content.partialShipment);
//             $("#placeOdDischarge").val(res.content.placeOfDischarge);
//             $("#lcOpenDays").val(res.content.lcOpenDays);
//             $("#paymentAtDays").val(res.content.paymentDays);
//             $("#shipmentWithinDays").val(res.content.shipmentWithinDays);
//             $("#chalanNo").val(res.content.challanNo);
//             $("#chalanDate").val(res.content.challanDate);
//             $("#truckChalanSerialNo").val(res.content.truckChallanSerialNo);
//             $("#truckNo").val(res.content.truckNo);
//             $("#truckChalanDate").val(res.content.truckChallanDate);
//             $("#vatNo").val(res.content.vatNo);
//             $("#tinNo").val(res.content.tinNo);
//             $("#date").val(res.content.date);
//             $("#finalDestination").val(res.content.finalDestination);
//             $("#modeOfShipment").val(res.content.shipmentMode);
//             if (res.content.goodsList.length == 1) {
//                 $("#goodsDesc").val(res.content.goodsList[0].id);
//             } else {
//                 var len = res.content.goodsList.length;
//                 var i = 0, j;
//                 for (j = 0; j < len; j++){
//                     if (j == 0) {
//                         $("#goodsDesc").val(res.content.goodsList[j].id);
//                     } else {
//                         i++;
//                         var total_element = $(".element").length;
//                         // last <div> with element class id
//                         var lastid = $(".element:last").attr("id");
//                         var split_id = lastid.split("_");
//                         var nextindex = (split_id[1]) + i;
//                         var max = 5;
//                         // Check total number elements
//                         if (total_element < max) {
//                             // Adding new div container after last occurance of element class
//                             $(".element:last").after("<div class='element' id='div_" + nextindex + "'></div>");
//                             // Adding element to <div>
//                             $("#div_" + nextindex).append("<br>" +
//                                 "<div class=\"border border-secondary\">" +
//                                 "<br>" +
//                                 "<div id='txt_" + nextindex + "'>" +
//                                 "<div class=\"form-group row\">" +
//                                 "<label for=\"goodsDesc\" class=\"col-sm-2 col-form-label\">Goods Desc.</label>" +
//                                 "<div class=\"col-sm-8\"><input   class=\"form-control select2\" name='goodsDesc_" + nextindex + "' id='goodsDesc_" + nextindex + "'/></div>" +
//                                 " <div class=\"col-sm-2\">\n" +
//                                 "  <button id='remove_" + nextindex + "' class='remove' type=\"button\" onclick=\"removeTotal(this.id)\"\n" +
//                                 " ><i class=\"fa fa-close\"></i>\n" +
//                                 "</button>\n" +
//                                 "</div>" +
//                                 "</div>" +
//                                 " <div class=\"form-group row\">\n" +
//                                 " <input name='goodsId_" + nextindex + "' type=\"hidden\" id='goodsId_" + nextindex + "'>" +
//                                 "                                                            <label for=\"goodQuantity\"\n" +
//                                 "                                                                   class=\"col-sm-2 col-form-label\">Quantity</label>\n" +
//                                 "                                                            <div class=\"col-sm-2\">\n" +
//                                 "                                                                <input type=\"text\" class=\"form-control\"\n" +
//                                 "                                              name='goodsQuantity_" + nextindex + "' id='goodsQuantity_" + nextindex + "'>\n" +
//                                 "                                                            </div>\n" +
//                                 "                                                            <label for=\"goodsUnitPrice\"\n" +
//                                 "                                                                   class=\"col-sm-2 col-form-label\">Unit\n" +
//                                 "                                                                Price</label>\n" +
//                                 "                                                            <div class=\"col-sm-2\">\n" +
//                                 "                                                                <input type=\"text\" class=\"form-control\"\n" +
//                                 "                                                                      id='goodsUnitPrice_" + nextindex + "' name='goodsUnitPrice_" + nextindex + "'>\n" +
//                                 "                                                            </div>\n" +
//                                 "                                                            <label for=\"goodsUnit\"\n" +
//                                 "                                                                   class=\"col-sm-2 col-form-label\">Unit</label>\n" +
//                                 "                                                            <div class=\"col-sm-2\">\n" +
//                                 "                                                                <input type=\"text\" class=\"form-control\"\n" +
//                                 "                                                                       id='goodsUnit_" + nextindex + "' name='goodsUnit_" + nextindex + "'>\n" +
//                                 "                                                            </div>\n" +
//                                 "                                                        </div>\n" +
//                                 "                                                        <div class=\"form-group row\">\n" +
//                                 "                                                            <label for=\"goodPackOrMarks\"\n" +
//                                 "                                                                   class=\"col-sm-2 col-form-label\">Pack\n" +
//                                 "                                                                / Marks</label>\n" +
//                                 "                                                            <div class=\"col-sm-2\">\n" +
//                                 "                                                                <input type=\"text\" class=\"form-control\"\n" +
//                                 "                                                                       id='goodPackOrMarks_" + nextindex + "' name='goodPackOrMarks_" + nextindex + "'>\n" +
//                                 "                                                            </div>\n" +
//
//                                 "                                                            <label for=\"goodsAmount\"\n" +
//                                 "                                                                   class=\"col-sm-2 col-form-label\">Total\n" +
//                                 "                                                                Amount</label>\n" +
//                                 "                                                            <div class=\"col-sm-2\">\n" +
//                                 "                                                                <input type=\"text\" class=\"form-control\"\n" +
//                                 "                                                                        id='goodsAmount_" + nextindex + "'>\n" +
//                                 "                                                            </div>\n" +
//                                 "                                                        </div>" +
//
//                                 "</div>" +
//                                 "</div>" +
//                                 "<br>");
//                         }
//                         $("#goodsId_id1"+i).val(res.content.goodsList[j].id);
//                         sectionDropdown(res.content.goodsList[j].id,nextindex);
//                         att="goodsDesc_id1"+i;
//                         goodsDate(res.content.goodsList[j].id,att);
//                         // $("#goodsDesc_id1"+i).val(res.content.goodsList[j].id).change();
//
//                     }
//
//                 }
//
//             }
//
//
//         }
//     });
// }

function updateSales(id) {
    refreshForm();
    // var data = dt.row($(btn).parents('tr')).data();
    // var id = data[0];
    selectedUserId = id;
    var api = rootSales + '/' + id;
    $.ajax({
        type: "GET",
        url: api,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {

            $("#proformaInvoiceNo").val(res.content.proformaInvoiceNo);
            $("#piDate").val(res.content.piDate);
            if (res.content.customer != null) {
                $("#purchaserName").val(res.content.customer.name);
                getCustById(res.content.customer.id);
            }
            $("#bankName").val(res.content.advisingBank.name);
            $("#lcBankName").val(res.content.lcBank.name);
            $("#advisingBranch").val(res.content.advisingBranch);
            $("#lcBranch").val(res.content.lcBranch);
            $("#billOfExcDate").val(res.content.exchangeDate);
            $("#lcDays").val(res.content.lcDays);
            $("#lcNo").val(res.content.lcNo);
            $("#expLcNo").val(res.content.exportLcNo);
            $("#lcDate").val(res.content.lcDate);
            $("#countryOrigin").val(res.content.county);
            $("#placePfLanding").val(res.content.loadingPlace);
            $("#partialShipment").val(res.content.partialShipment);
            $("#placeOdDischarge").val(res.content.placeOfDischarge);
            $("#lcOpenDays").val(res.content.lcOpenDays);
            $("#paymentAtDays").val(res.content.paymentDays);
            $("#shipmentWithinDays").val(res.content.shipmentWithinDays);
            $("#chalanNo").val(res.content.challanNo);
            $("#chalanDate").val(res.content.challanDate);
            $("#truckChalanSerialNo").val(res.content.truckChallanSerialNo);
            $("#truckNo").val(res.content.truckNo);
            $("#truckChalanDate").val(res.content.truckChallanDate);
            $("#vatNo").val(res.content.vatNo);
            $("#tinNo").val(res.content.tinNo);
            $("#date").val(res.content.date);
            $('#invoiceNo').val(res.content.invoiceNo);
            $("#finalDestination").val(res.content.finalDestination);
            $("#modeOfShipment").val(res.content.shipmentMode);
            $("#goodsTotalAmount").val(res.content.totalAmount);

            var len = res.content.goodsList.length;
            var i = 0, j;
            for (j = 0; j < len; j++) {
                var rowCount = Math.floor((Math.random() * 10000) + 1);

                var tbody = $('#myTable').children('tbody');
                var table = tbody.length ? tbody : $('#myTable');
                var row = '<tr id="row_' + rowCount + '">' +
                    '<td style="display:none;">{{id}}</td>' +
                    '<td id="goodDesc_' + rowCount + '">{{description}}</td>' +
                    '<td id="goodQuantity_' + rowCount + '">{{quantity}}</td>' +
                    '<td id="goodUnitPrice_' + rowCount + '">{{unitPrice}}</td>' +
                    '<td id="goodUnit_' + rowCount + '">{{unit}}</td>' +
                    '<td id="goodAmount_' + rowCount + '">{{totalAmount}}</td>' +
                    '<td id="goodPackMarks_' + rowCount + '">{{packMarks}}</td>' +
                    '<td><i value="Delete" type="button" alt="Delete6" class="deleteIcon fa fa-trash"></i></button></td>'
                '</tr>';
                //Add row
                var q = res.content.goodsList[j].quantity;
                var u = res.content.goodsList[j].unitPrice;
                table.append(row.compose({
                    'id': rowCount,
                    'description': res.content.goodsList[j].description,
                    'quantity': res.content.goodsList[j].quantity,
                    'unitPrice': res.content.goodsList[j].unitPrice,
                    'unit': res.content.goodsList[j].unit,
                    'totalAmount': q * u,
                    'packMarks': res.content.goodsList[j].packOrMarks

                }));

            }


        }
    });
}

function getAllByPerforma(pin) {
    if (pin == null || pin == "") {
        return;
    }
    // refreshForm();

    var api = rootSales + '/name?pin=' + encodeURIComponent(pin);
    $.ajax({
        type: "GET",
        url: api,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            if (res.statusCode >= 400) {
                return;
            }
            hideBtn();
            selectedUserId = res.content.id;
            $("#proformaInvoiceNo").val(res.content.proformaInvoiceNo);
            $("#piDate").val(res.content.piDate);
            if (res.content.customer != null) {
                $("#purchaserName").val(res.content.customer.name);
                getCustById(res.content.customer.id);
            }

            $("#bankName").val(res.content.advisingBank.name);
            $("#lcBankName").val(res.content.lcBank.name);
            $("#advisingBranch").val(res.content.advisingBranch);
            $("#lcBranch").val(res.content.lcBranch);
            $("#billOfExcDate").val(res.content.exchangeDate);
            $("#lcDays").val(res.content.lcDays);
            $("#lcNo").val(res.content.lcNo);
            $("#expLcNo").val(res.content.exportLcNo);
            $("#lcDate").val(res.content.lcDate);
            $("#countryOrigin").val(res.content.county);
            $("#placePfLanding").val(res.content.loadingPlace);
            $("#partialShipment").val(res.content.partialShipment);
            $("#placeOdDischarge").val(res.content.placeOfDischarge);
            $("#lcOpenDays").val(res.content.lcOpenDays);
            $("#paymentAtDays").val(res.content.paymentDays);
            $("#shipmentWithinDays").val(res.content.shipmentWithinDays);
            $("#chalanNo").val(res.content.challanNo);
            $("#chalanDate").val(res.content.challanDate);
            $("#truckChalanSerialNo").val(res.content.truckChallanSerialNo);
            $("#truckNo").val(res.content.truckNo);
            $("#truckChalanDate").val(res.content.truckChallanDate);
            $("#vatNo").val(res.content.vatNo);
            $("#tinNo").val(res.content.tinNo);
            $("#date").val(res.content.date);
            $('#invoiceNo').val(res.content.invoiceNo);
            $("#finalDestination").val(res.content.finalDestination);
            $("#modeOfShipment").val(res.content.shipmentMode);
            $("#goodsTotalAmount").val(res.content.totalAmount);

            var len = res.content.goodsList.length;
            var i = 0, j;
            for (j = 0; j < len; j++) {
                var rowCount = Math.floor((Math.random() * 10000) + 1);

                var tbody = $('#myTable').children('tbody');
                var table = tbody.length ? tbody : $('#myTable');
                var row = '<tr id="row_' + rowCount + '">' +
                    '<td style="display:none;">{{id}}</td>' +
                    '<td id="goodDesc_' + rowCount + '">{{description}}</td>' +
                    '<td id="goodQuantity_' + rowCount + '">{{quantity}}</td>' +
                    '<td id="goodUnitPrice_' + rowCount + '">{{unitPrice}}</td>' +
                    '<td id="goodUnit_' + rowCount + '">{{unit}}</td>' +
                    '<td id="goodAmount_' + rowCount + '">{{totalAmount}}</td>' +
                    '<td id="goodPackMarks_' + rowCount + '">{{packMarks}}</td>' +
                    '<td><i value="Delete" type="button" alt="Delete6" class="deleteIcon fa fa-trash"></i></button></td>'
                '</tr>';
                //Add row
                var q = res.content.goodsList[j].quantity;
                var u = res.content.goodsList[j].unitPrice;
                table.append(row.compose({
                    'id': rowCount,
                    'description': res.content.goodsList[j].description,
                    'quantity': res.content.goodsList[j].quantity,
                    'unitPrice': res.content.goodsList[j].unitPrice,
                    'unit': res.content.goodsList[j].unit,
                    'totalAmount': q * u,
                    'packMarks': res.content.goodsList[j].packOrMarks

                }));

            }


        }
    });
}


function getAllSales() {
    var editBtn = '<button type="button" class="btn btn-success save" onclick="updateSales(this),hideBtn()">Edit</button>';
    var removeBtn = '<button type="button" class="btn btn-danger remove" onclick="removeSales(this)">Remove</button>';
    var count = 0;
    var api = rootSales + '/all';
    var tbody = $('#myTable2').children('tbody');
    var table = tbody.length ? tbody : $('#myTable');
    var row = '<tr >' +
        '<td style="display: none">{{id}}</td>' +
        '<td>{{serial}}</td>' +
        '<td >{{performaInvoice}}</td>' +
        '<td >{{piDate}}</td>' +

        '<td><button type="button" class="btn btn-success " onclick="updateSales({{editId}}),hideBtn()"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button> <button type="button" class="btn btn-danger " onclick="removeSales({{editId}})"><i class="fa fa-trash" aria-hidden="true"></i></button></td>' +
        '</tr>';

    $.ajax({
        type: "GET",
        url: api,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            // $(" td:nth-child(5)").hide();

            var totalData = res.totalCount;
            var totalFractionalPage = totalData / 20;
            var totalPage = Math.ceil(totalFractionalPage);
            $('#myTable2').after('<div id="nav" class="pagination"></div>');
            for (var i = 0; i < res.content.length; i++) {
                count++;
                table.append(row.compose({
                    'id': res.content[i].id,
                    'serial': count,
                    'performaInvoice': res.content[i].proformaInvoiceNo,
                    'piDate': res.content[i].piDate,
                    'editId': res.content[i].id
                }));
            }
            for (i = 0; i < totalPage; i++) {
                var pageNum = i + 1;
                $('#nav').append('<button type="button" class="btn btn-success save" id="pagination" value="' + i + '" onclick="getVal(this.value,this.id)">' + pageNum + '</button> ');
            }
            // $('#nav').append('<button type="button">&raquo;</button>');
            // $('#mytable2 td:nth-child(0),th:nth-child(0)').hide();
            //
            //
            // $('#myTable2_wrapper').attr("hidden", "hidden");
            // var tableHead = "<thead id='theadUser'></thead>" +
            //     "<tbody id='tbodyUser'></tbody>"
            // $('#myTable2').html(tableHead);
            // $('#myTable2_wrapper').removeAttr("hidden", "hidden");
            // destroyTable('myTable2');
            // var headArr = ['#', 'Performa Invoice', 'Performa Date', 'Action'];
            // createHeader('theadUser', headArr);
            // prepareListDataGrid(res, 'tbodyUser');
            // createWithButton('myTable2', [0, 1, 2], "Sales List", [5, 10, 30, 50, 'All'])

        }

    });
}


function getVal(val, id) {

    var rowCount = myTable2.rows.length;
    for (var i = rowCount - 1; i > 0; i--) {
        myTable2.deleteRow(i);
    }
    var api;
    var count;
    if (id === "searchValue") {
        api = rootSales + '/all?search=' + val;
        count = 0;
    }

    if (id === "pagination") {
        api = rootSales + '/all?page=' + val;
        count = val * 20
    }

    var tbody = $('#myTable2').children('tbody');
    var table = tbody.length ? tbody : $('#myTable');
    var row = '<tr >' +
        '<td>{{id}}</td>' +
        '<td>{{serial}}</td>' +
        '<td >{{performaInvoice}}</td>' +
        '<td >{{piDate}}</td>' +

        '<td><button type="button" class="btn btn-success " onclick="updateSales({{editId}}),hideBtn()"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button> <button type="button" class="btn btn-danger " onclick="removeSales({{editId}})"><i class="fa fa-trash" aria-hidden="true"></i></button></td>' +
        '</tr>';

    $.ajax({
        type: "GET",
        url: api,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {


            for (var i = 0; i < res.content.length; i++) {
                count++;
                table.append(row.compose({
                    'id': res.content[i].id,
                    'serial': count,
                    'performaInvoice': res.content[i].proformaInvoiceNo,
                    'piDate': res.content[i].piDate,
                    'editId': res.content[i].id
                }));
            }

        }

    });
    // prevent default browser refresh on "#" link

}


function prepareListDataGrid(response, tbodyUser) {
    var count = 0;
    for (var i = 0; i < response.content.length; i++) {

        count++;
        var trTableBody = $('<tr class="text-center"></tr>').appendTo($('#' + tbodyUser));
        $('<td>' + count + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response.content[i].proformaInvoiceNo) + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response.content[i].piDate) + '</td>').appendTo(trTableBody);

        $('<td><button class="btn btn-info" type="button" onclick="updateSales(' + response.content[i].id + ');hideBtn()"><b><i class="fa fa-eye" aria-hidden="true"></i></b></button>').appendTo(trTableBody);


    }
}

$(function () {
    $('#lcBankName').keyup(function () {
        this.value = this.value.toLocaleUpperCase();
    });
});

$(function () {
    $('#expLcNo').keyup(function () {
        this.value = this.value.toLocaleUpperCase();
    });
});

$(function () {
    $('#bankName').keyup(function () {
        this.value = this.value.toLocaleUpperCase();
    });
});

$(function () {
    $('#proformaInvoiceNo').keyup(function () {
        this.value = this.value.toLocaleUpperCase();
    });
});
$(function () {
    $('#invoiceNo').keyup(function () {
        this.value = this.value.toLocaleUpperCase();
    });
});
$(function () {
    $('#truckNo').keyup(function () {
        this.value = this.value.toLocaleUpperCase();
    });
});

$(function () {
    $('#goodsUnit').keyup(function () {
        this.value = this.value.toLocaleUpperCase();
    });
});

function white_space(field) {
    field.value = field.value.replace(/^\\s+/, "");
}

$(function () {
    $('#countryOrigin').val('BANGLADESH');
    $('#placePfLanding').val('Suppliers Factory');
    $('#placeOdDischarge').val('Buyers Factory');
    $('#finalDestination').val('Buyers Factory');
});

window.onload = function () {

    getDatePicker('chalanDate');
    getDatePicker('truckChalanDate');
    getDatePicker('date');
    getDatePicker('lcDate');
    getDatePicker('billOfExcDate');
    getDatePicker('piDate');
};
