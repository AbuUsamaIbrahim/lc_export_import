var host = '206.189.133.35';
// var host = 'localhost';
// var port=8080;
var port = 8090;
// var contextPath = 'lcExportImport';
// var basicUrl = 'http://' + host + ':' + port + '/' + contextPath;
var basicUrl = 'http://' + host + ':' + port;

function logout() {
    window.localStorage.removeItem('user_info');
    window.location = 'index.html';
}

function toFixed1(x) {
    if (Math.abs(x) < 1.0) {
        var e = parseInt(x.toString().split('e-')[1]);
        if (e) {
            x *= Math.pow(10, e - 1);
            x = '0.' + (new Array(e)).join('0') + x.toString().substring(2);
        }
    } else {
        var e = parseInt(x.toString().split('+')[1]);
        if (e > 20) {
            e -= 20;
            x /= Math.pow(10, e);
            x += (new Array(e + 1)).join('0');
        }
    }
    return x;
}

function getDatePicker(eId) {
    $('#' + eId).daterangepicker({
        singleDatePicker: true,
        showDropdowns: true,
        minYear: 2007,
        maxYear: parseInt(moment().format('YYYY'), 10) + 10,
        locale: {
            format: 'DD/MM/YYYY'
        }
    });
    $('#' + eId).val('');
}

function checkValue(val) {
    if (val == '' || val == undefined || val == 'undefined' || val == null || val == 'null') {
        val = '-';
    }
    return val;
}

function destroyTable(tableId) {
    if ($.fn.DataTable.isDataTable('#' + tableId)) {
        $('#' + tableId).DataTable().destroy();
    }
}

function createHeader(headID, headArr) {
    $('#' + headID).empty();
    var trHead = $('<tr class="text-center"></tr>').appendTo($('#' + headID));

    for (var i = 0; i < headArr.length; i++) {
        $('<td><b><span class="trn">' + headArr[i] + '</span></b></td>').appendTo(trHead);
    }
}

function createWithButton(tableId, outputColumnArray, title, lengthMenuArray) {
    var actualLengthMenuArray = [];
    if (lengthMenuArray.length > 0) {
        for (var k = 0; k < lengthMenuArray.length; k++) {
            if (lengthMenuArray[k] != 'All') {
                actualLengthMenuArray.push(lengthMenuArray[k]);
            } else {
                actualLengthMenuArray.push(-1);
            }
        }
    }
    var table = $('#' + tableId).DataTable({
        "iCookieDuration": 60,
        "bStateSave": false,
        "bAutoWidth": false,
        "bScrollAutoCss": true,
        "bProcessing": true,
        "bRetrieve": true,
        "bJQueryUI": true,
        "sScrollX": "100%",
        "bScrollCollapse": true,
        "searchHighlight": true,
        "lengthMenu": [actualLengthMenuArray, lengthMenuArray],
        "order": [[0, "asc"]],
        dom: 'Blfrtip',
        buttons: [
            {
                extend: 'excel',
                text: 'Excel',
                className: 'btn btn-primary',
                exportOptions: {
                    columns: outputColumnArray
                },
                alignment: 'center',
                orientation: 'landscape',
                title: title

            },
            {
                extend: 'pdfHtml5',
                text: 'PDF',
                className: 'btn btn-primary',
                exportOptions: {
                    columns: outputColumnArray
                },
                alignment: 'center',
                orientation: 'landscape',
                title: title
                /*customize: function(doc) {
                 doc.defaultStyle={font : 'Kalpurush'};
                 }*/
            },
            {
                extend: 'print',
                text: 'Print',
                className: 'btn btn-primary',
                exportOptions: {
                    columns: outputColumnArray
                },
                alignment: 'center',
                orientation: 'landscape',
                title: title
            }
        ]
    });

    return table;
}

String.prototype.compose = (function () {
    var re = /\{{(.+?)\}}/g;
    return function (o) {
        return this.replace(re, function (_, k) {
            return typeof o[k] != 'undefined' ? o[k] : '';
        });
    }
}());
