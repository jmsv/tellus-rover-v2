from flask import Flask, request
import shortuuid

app = Flask(__name__)


password = 'potato'
auth_keys = []


@app.route('/login', methods=['POST'])
def login():
    password_attempt = ''
    try:
        password_attempt = request.form['password']
        print 'attempt: ' + password_attempt
    except Exception as e:
        if str(e).startswith('400 Bad Request'):
            return 'no password'
        else:
            return e

    if password_attempt == password:
        key = shortuuid.uuid()
        global auth_keys
        auth_keys.append(key)
        return key, 200
    else:
        return 'invalid password', 401

    # If none of the above is returned (should never happen)
    return 'error', 500


def main():
    app.run('0.0.0.0')


if __name__ == '__main__':
    main()
