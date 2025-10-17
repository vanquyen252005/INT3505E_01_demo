from flask import Flask
from config import Config
from models import db
from Rest_Library_Flask_version2.routes.admin import admin_bp
from Rest_Library_Flask_version2.routes.user import user_bp

app = Flask(__name__)
app.config.from_object(Config)

db.init_app(app)

# Đăng ký blueprint (route tách riêng)
app.register_blueprint(admin_bp, url_prefix="/admin")
app.register_blueprint(user_bp, url_prefix="/user")

if __name__ == "__main__":
    with app.app_context():
        db.create_all()
    app.run(debug=True)
