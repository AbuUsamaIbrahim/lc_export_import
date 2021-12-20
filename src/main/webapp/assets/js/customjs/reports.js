var rootFiles = basicUrl + '/api/v1/files';
var rootSales = basicUrl + '/api/v1/sales';
var user_info = JSON.parse(window.localStorage.getItem('user_info'));
document.cookie = 'access_token=[user_info.token]';
$(document).ready(function () {
    getAllSales(null, null);
});

function base64ToArrayBuffer(base64) {

    var binaryString = window.atob(Base64.decode(base64));
    var binaryLen = binaryString.length;
    var bytes = new Uint8Array(binaryLen);
    for (var i = 0; i < binaryLen; i++) {
        var ascii = binaryString.charCodeAt(i);
        bytes[i] = ascii;
    }
    return bytes;
}

function saveByteArray(reportName, byte) {

    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    var fileName = reportName;
    link.download = fileName;
    link.click();
};

function dowloadTruckChalan(reportName, id) {
    // var data = dt.row($(btn).parents('tr')).data();
    // var id = data[0];
    var api = rootFiles + "/download?sales_id=" + id + "&report_type=" + reportName;
    var link = document.createElement('a');
    link.href = api;
    link.download = reportName;
    link.click();

}

function showModal(id) {
    // var data = dt.row($(btn).parents('tr')).data();
    // var id = data[0];

    $('#salesId').val(id);


    $('#exampleModal').modal().show();
}

function downloadReport() {
    var reportName = "Application";
    var bank = $('#bname').val();
    var branch = $('#branchName').val();
    var address = $('#address').val();
    var account = $('#account').val();
    var id = $('#salesId').val();
    var api = rootFiles + "/download/params?sales_id=" + id + "&bank=" + bank + "&branch=" + branch + "&address=" + address + "&account=" + account;
    var link = document.createElement('a');
    link.href = api;
    link.download = reportName;
    link.click();

}

function getAllSales(val, elementId) {

    var truckChalan = '<button type="button" class="btn btn-success save" id="truckChalan" onclick="dowloadTruckChalan(this.id,{{editId}})">Truck Chalan</button>';
    var delivery = '<button type="button" class="btn btn-success save" id="delivery" onclick="dowloadTruckChalan(this.id,{{editId}})">Delivery Chalan</button>';
    var packingList = '<button type="button" class="btn btn-success save" id="packingList" onclick="dowloadTruckChalan(this.id,{{editId}})">Pack List</button>';
    var commercialInvoice = '<button type="button" class="btn btn-success save" id="commercialInvoice" onclick="dowloadTruckChalan(this.id,{{editId}})">C.Invoice</button>';
    var performaInvoice = '<button type="button" class="btn btn-success save" id="performaInvoice" onclick="dowloadTruckChalan(this.id,{{editId}})">P.Invoice</button>';
    var billOfExc = '<button type="button" class="btn btn-success save" id="billOfExchange" onclick="dowloadTruckChalan(this.id,{{editId}})">Bill of Exc.</button>';
    var origin = '<button type="button" class="btn btn-success save" id="certificateOfOrigin" onclick="dowloadTruckChalan(this.id,{{editId}})">Cert. Ori.</button>';
    var benefi = '<button type="button" class="btn btn-success save" id="certificateOfBenefi" onclick="dowloadTruckChalan(this.id,{{editId}})">Cert. Benef</button>';
    var application = '<button type="button" class="btn btn-success save" id="application" onclick="showModal({{editId}})">Application</button>';

    var rowCount = myTable2.rows.length;
    for (var i = rowCount - 1; i > 0; i--) {
        myTable2.deleteRow(i);
    }
    var count = 0;
    var api;
    if (val != null && elementId != null) {
        if (elementId === "searchValue") {
            api = rootSales + '/all?search=' + val;
            count = 0;
        }

        if (elementId === "pagination") {
            api = rootSales + '/all?page=' + val;
            count = val * 20
        }
    }
    if (val == null && elementId == null) {
        api = rootSales + '/all';
    }
    var tbody = $('#myTable2').children('tbody');
    var table = tbody.length ? tbody : $('#myTable');
    var row = '<tr >' +
        '<td>{{id}}</td>' +
        '<td>{{serial}}</td>' +
        '<td >{{performaInvoice}}</td>' +
        '<td >{{piDate}}</td>' +

        '<td>' + billOfExc + ' ' + delivery + ' ' + truckChalan + ' ' + commercialInvoice + ' ' + performaInvoice + ' ' + packingList + ' ' + origin + ' ' + benefi + ' ' + application + '</td>' +
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
            // $('#myTable2').after('<div id="nav" class="pagination"><a href="#">&laquo;</a></div>');
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
            if (val == null && elementId == null) {
                $('#myTable2').after('<div id="nav" class="pagination"></div>');
                for (i = 0; i < totalPage; i++) {
                    var pageNum = i + 1;
                    $('#nav').append('<button type="button" class="btn btn-success save" id="pagination" value="' + i + '" onclick="getAllSales(this.value,this.id)">' + pageNum + '</button> ');
                }
                // $('#nav').append('<a href="#">&raquo;</a>');
            }


        }

    });
}

