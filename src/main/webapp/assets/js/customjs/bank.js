var rootBank = basicUrl + '/api/v1/banks';
var user_info = JSON.parse(window.localStorage.getItem('user_info'));
var dt;

function saveBank() {
    var bankId = $('#id').val();
    var name = $('#bname').val();
    var payload = {id: bankId, name: name};
    var api = "";
    if (selectedUserId == 0) {
        api = rootBank + '/create';
    } else {
        api = rootBank + '/' + selectedUserId;
    }
    if (name == 'undefined' || name.length == 0) {
        alert('Mendatory fields should not be empty');
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
                $("#exampleModal").modal('hide');
                var msg = selectedUserId == 0 ? 'Bank Created Successfully' : 'Bank Updated Successfully';
                alert(msg);
                getAllBank();
            }

        });
    }
}


var userList = [];
var selectedUserId = 0;
$(document).ready(function () {
    var user_info = JSON.parse(window.localStorage.getItem('user_info'));
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    getAllBank();
});

function showModal(bank) {
    if (bank == null) {
        selectedUserId = 0;
        $("#id").val("");
        $("#bname").val("");
    } else {
        selectedUserId = bank.id;
        $("#id").val(bank.id);
        $("#bname").val(bank.bname);
    }
    $('#exampleModal').modal().show();
}


function updateBank(btn) {
    var data = dt.row($(btn).parents('tr')).data();
    var id = data[0];
    var name = data[2];
    var bank = {id: id, bname: name};
    showModal(bank);
}

function removeBank(btn) {
    var data = dt.row($(btn).parents('tr')).data();
    var id = data[0];
    var api = rootBank + '/' + id;
    if (window.confirm('Are you sure to delete bank?')) {
        $.ajax({
            type: "DELETE",
            url: api,
            headers: {'Authorization': 'Bearer ' + user_info.token},
            contentType: 'application/json',
            error: function (res) {
                alert(JSON.stringify(res));
            },
            success: function (res) {
                alert('Bank Deleted Successfully');
                getAllBank();
            }
        });
    }
}


function getAllBank(val, elementId) {
    var editBtn = '<button type="button" class="btn btn-success save" onclick="updateBank(this)">Edit</button>';
    var removeBtn = '<button type="button" class="btn btn-danger remove" onclick="removeBank(this)">Remove</button>';


    var api = rootBank + "/all";
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


function logout() {
    window.localStorage.removeItem('user_info');
    window.location = 'index.html';
}
