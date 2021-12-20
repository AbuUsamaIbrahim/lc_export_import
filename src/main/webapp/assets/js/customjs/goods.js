var rootPath = basicUrl + '/api/v1/goods';
var goodsList = [];
var selectedUserId = 0;
var user_info = JSON.parse(window.localStorage.getItem('user_info'));
var dt;

$(document).ready(function () {
    var user_info = JSON.parse(window.localStorage.getItem('user_info'));
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    getAllGoods();
});

function showModal(goods) {
    if (goods == null) {
        $("#exampleModalLabel").html("Add Goods");
        selectedUserId = 0;
        $("#id").val("");
        $("#quantity").val("");
        $("#unitPrice").val("");
        $("#unit").val("");
        $("#packOrMarks").val("");
        $("#description").val("");
        $("#remarks").val("");

    } else {
        selectedUserId = goods.id;

        $("#id").val(goods.id);
        $("#quantity").val(goods.quantity);
        $("#unitPrice").val(goods.unitPrice);
        $("#unit").val(goods.unit);
        $("#packOrMarks").val(goods.packOrMarks);
        $("#description").val(goods.description);
        $("#remarks").val(goods.remarks);
    }
    $('#exampleModal').modal().show();
}

function saveGoods() {
    var id = $("#id").val();
    var quantity = $("#quantity").val();
    var unitPrice = $("#unitPrice").val();
    var unit = $("#unit").val();
    var packOrMarks = $("#packOrMarks").val();
    var description = $("#description").val();
    var remarks = $("#remarks").val();
    var api = "";
    if (selectedUserId == 0) {
        api = rootPath + '/create';
    } else {
        api = rootPath + '/' + selectedUserId;
    }
    console.log(api);
    var payload = {
        id: id,
        quantity: quantity,
        unitPrice: unitPrice,
        unit: unit,
        packOrMarks: packOrMarks,
        description: description,
        remarks: remarks
    };
    if (quantity == 'undefined' || quantity.length == 0 || unitPrice == 'undefined' || unitPrice.length == 0 || unit == 'undefined' || unit.length == 0
        || packOrMarks == 'undefined' || packOrMarks.length == 0 || description == 'undefined' || description.length == 0 || remarks == 'undefined' || remarks.length == 0) {
        alert('Mandatory fields should not be empty');
    } else {
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
                var msg = selectedUserId == 0 ? 'Goods Created Successfully' : 'Goods Updated Successfully';
                alert(msg);
                $("#exampleModal").modal('hide');
                getAllGoods();
            }

        });
    }

}

function updateGoods(btn) {
    var data = dt.row($(btn).parents('tr')).data();
    var id = data[0];
    var quantity = data[2];
    var unitPrice = data[5];
    var unit = data[6];
    var packOrMarks = data[4];
    var description = data[3];
    var remarks = data[7];
    var goods = {
        id: id,
        quantity: quantity,
        unitPrice: unitPrice,
        unit: unit,
        packOrMarks: packOrMarks,
        description: description,
        remarks: remarks
    };
    showModal(goods);
}

function removeGoods(btn) {
    var data = dt.row($(btn).parents('tr')).data();
    var id = data[0];
    if (data != null && data[3] == 'admin@abc.com') {
        alert('Admin User is not deletable');
        return;
    }
    var api = rootPath + '/' + id;
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
                alert('Goods Deleted Successfully');
                getAllGoods();
            }
        });
    }
}


function getAllGoods() {
    var editBtn = '<button type="button" class="btn btn-success save" onclick="updateGoods(this)">Edit</button>';
    var removeBtn = '<button type="button" class="btn btn-danger remove" onclick="removeGoods(this)">Remove</button>';

    var api = rootPath + '/all';

    $.ajax({
        type: "GET",
        url: api,
        headers: {'Authorization': 'Bearer ' + user_info.token},
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            var dataSet = [];
            var i = 1;
            $.each(res.content, function (k, v) {
                var arr = [];
                arr.push(v.id);
                arr.push(i++);
                arr.push(v.quantity);
                arr.push(v.description);
                arr.push(v.packOrMarks);
                arr.push(v.unitPrice);
                arr.push(v.unit);
                arr.push(v.remarks);
                arr.push(editBtn + ' ' + removeBtn);
                dataSet.push(arr);
            });

            goodsList = dataSet;

            $('#resultContainer').empty();
            $('<table id="example" class="table table-striped table-bordered table-sm" width="100%"></table>').appendTo('#resultContainer');
            dt = $('#example').DataTable({

                data: dataSet,
                columns: [
                    {title: "Id"},
                    {title: "Serial Number"},
                    {title: "Quantity"},
                    {title: "Desc."},
                    {title: "Pack Or Marks"},
                    {title: "Unit Price"},
                    {title: "Unit"},
                    {title: "Remarks"},
                    {title: "Actions"}
                ],
                "columnDefs": [
                    {
                        "targets": [0],
                        "visible": false,
                        "searchable": false
                    }
                ]
            });
        }

    });
}

