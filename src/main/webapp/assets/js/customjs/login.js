function login() {
    var login_email = $('#login_email').val();
    var login_password = $('#login_password').val();

    if (login_email == "" || login_password == "") {
        alert('Please Fill-Up The Input ');
    } else {
        var api = basicUrl + '/api/auth/login';
        var payload = {emailOrUserName: login_email, password: login_password};
        $.ajax({
            type: "POST",
            url: api,
            data: JSON.stringify(payload),
            contentType: 'application/json',
            error: function (res) {
                alert("Incorrect Username Or Password")
            },
            success: function (res) {
                const user = {
                    name: res.content.userName,
                    token: res.content.token
                }
                window.localStorage.setItem('user_info', JSON.stringify(user));
                window.location = 'bank.html';
            }
        });

    }

}