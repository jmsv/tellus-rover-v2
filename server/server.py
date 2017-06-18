from flask import Flask, request

app = Flask(__name__)


password = 'potato'


@app.route('/login', methods=['POST'])
def login():
    password_attempt = ''
    try:
        password_attempt = request.form['password']
        print 'attempt: ' + password_attempt
    except Exception as e:
        if str(e).startswith('400 Bad Request'):
            return 'No valid password'
        else:
            return e

    if password_attempt == password:
        return 'Correct password', 200  # This should return access key
    else:
        return 'Invalid password', 401

    # If none of the above is returned (should never happen)
    return 'Error', 500


def main():
    app.run('0.0.0.0')


if __name__ == '__main__':
    main()
