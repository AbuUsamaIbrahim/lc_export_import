var rootBank = basicUrl + '/api/v1/banks';
var rootBranch = basicUrl + '/api/v1/branches';
var branchList = [];
var selectedUserId = 0;
var user_info = JSON.parse(window.localStorage.getItem('user_info'));
var dt;

$(document).ready(function () {
    var user_info = JSON.parse(window.localStorage.getItem('user_info'));
    if (user_info == null) {
        window.location = 'index.html';
    }
    $('#pUser').html(user_info.name);
    getAllBranch();
    var api = rootBank + '/all';
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
            var selectOption = new Option("--Select One--", "", false, false);
            $('#bank').append(selectOption);
            var len = res.content.length;
            for (var i = 0; i < len; i++) {
                var options = new Option(res.content[i].name, res.content[i].id, false, false);
                $('#bank').append(options);
            }
        }
    });

});

function addBranch() {
    var id = $("#id").val();
    var name = $("#branchName").val();
    var address = $("#branchName").val();
    var bank = $("#bank").val();

    var api = "";
    if (selectedUserId == 0) {
        api = rootBranch + '/create';
    } else {
        api = rootBranch + '/' + selectedUserId;
    }
    console.log(api);
    var payload = {id: id, name: name, address: address, bank: {id: bank}};
    if (name == 'undefined' || name.length == 0 || address == 'undefined' || address.length == 0) {
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
                var msg = selectedUserId == 0 ? 'User Created Successfully' : 'User Updated Successfully';
                alert(msg);
                $("#exampleModal").modal('hide');
                getAllBranch();
            }

        });
    }
}

function showModal(branch) {
    if (branch == null) {
        $("#exampleModalLabel").html("Add Branch");
        selectedUserId = 0;
        $("#id").val("");
        $("#branchName").val("");
        $("#branchAddress").val("");
        $("#bank").val("");

    } else {
        selectedUserId = branch.id;

        $("#id").val(branch.id);
        $("#branchName").val(branch.name);
        // $("#custName").prop("readonly", true);
        $("#branchAddress").val(branch.address);
        // $("#email").prop("readonly", true);
        $("#bank").val(branch.bank);
    }
    $('#exampleModal').modal().show();
}

function updateBranch(btn) {
    var data = dt.row($(btn).parents('tr')).data();
    var id = data[0];
    var name = data[2];
    var address = data[3];
    var bank = data[4];
    var branch = {id: id, name: name, address: address, bank: bank};
    showModal(branch);
}

function removeBranch(btn) {
    var data = dt.row($(btn).parents('tr')).data();
    var id = data[0];
    if (data != null && data[3] == 'admin@abc.com') {
        alert('Admin User is not deletable');
        return;
    }
    var api = rootBranch + '/' + id;
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
                alert('Branch Deleted Successfully');
                getAllBranch();
            }
        });
    }
}


function getAllBranch() {
    var editBtn = '<button type="button" class="btn btn-success save" onclick="updateBranch(this)">Edit</button>';
    var removeBtn = '<button type="button" class="btn btn-danger remove" onclick="removeBranch(this)">Remove</button>';

    var api = rootBranch + '/all';

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
                // arr.push(v.address);

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
                    {title: "Branch Name & Address"},
                    // {title: "Address"},

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