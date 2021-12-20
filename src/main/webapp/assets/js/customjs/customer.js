var rootPath = basicUrl + '/api/v1/customers';
var customerList = [];
var selectedUserId = 0;
var user_info = JSON.parse(window.localStorage.getItem('user_info'));
var dt;

$(document).ready(function () {
    var user_info = JSON.parse(window.localStorage.getItem('user_info'));
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    getAllCustomer();
});

function showModal(customer) {
    if (customer == null) {
        $("#exampleModalLabel").html("Add Customer");
        selectedUserId = 0;
        $("#id").val("");
        $("#custName").val("");
        $("#custAddress").val("");
        $("#custMobile").val("");

    } else {
        selectedUserId = customer.id;

        $("#id").val(customer.id);
        $("#custName").val(customer.name);
        // $("#custName").prop("readonly", true);
        $("#custAddress").val(customer.address);
        // $("#email").prop("readonly", true);
        $("#custMobile").val(customer.mobileNo);
    }
    $('#exampleModal').modal().show();
}

function saveCustomer() {
    var id = $("#id").val();
    var name = $("#custName").val();
    var address = $("#custAddress").val();
    var mobile = $("#custMobile").val();
    var api = "";
    if (selectedUserId == 0) {
        api = rootPath + '/create';
    } else {
        api = rootPath + '/' + selectedUserId;
    }
    console.log(api);
    var payload = {id: id, name: name, address: address, mobileNo: mobile};
    if (name == 'undefined' || name.length == 0 || address == 'undefined' || address.length == 0 || mobile == 'undefined' || mobile.length == 0) {
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
                var msg = selectedUserId == 0 ? 'Customer Created Successfully' : 'Customer Updated Successfully';
                alert(msg);
                $("#exampleModal").modal('hide');
                getAllCustomer();
            }

        });
    }

}

function updateCustomer(btn) {
    var data = dt.row($(btn).parents('tr')).data();
    var id = data[0];
    var name = data[2];
    var address = data[3];
    var mobile = data[4];
    var customer = {id: id, name: name, address: address, mobileNo: mobile};
    showModal(customer);
}

function removeCustomer(btn) {
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
                alert('User Deleted Successfully');
                getAllCustomer();
            }
        });
    }
}


function getAllCustomer() {
    var editBtn = '<button type="button" class="btn btn-success save" onclick="updateCustomer(this)">Edit</button>';
    var removeBtn = '<button type="button" class="btn btn-danger remove" onclick="removeCustomer(this)">Remove</button>';

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
                arr.push(v.name);
                arr.push(v.address);
                arr.push(v.mobileNo)
                arr.push(editBtn + ' ' + removeBtn);
                dataSet.push(arr);
            });

            customerList = dataSet;

            $('#resultContainer').empty();
            $('<table id="example" class="table table-striped table-bordered table-sm" width="100%"></table>').appendTo('#resultContainer');
            dt = $('#example').DataTable({

                data: dataSet,
                columns: [
                    {title: "Id"},
                    {title: "Serial Number"},
                    {title: "Name"},
                    {title: "Address"},
                    {title: "Mobile No."},
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

