from flask import Blueprint, render_template, request, redirect, url_for,jsonify,make_response
from models import db, Book, BorrowRecord
from datetime import datetime

admin_bp = Blueprint("admin", __name__)

@admin_bp.route("/books", methods=["GET"])
def books():
    books = Book.query.all()

    if request.is_json or "application/json" in request.headers.get("Accept", ""):
        books_data = []
        for book in books:
            books_data.append({
                "id": book.id,
                "title": book.title,
                "author": book.author,
                "_links": {
                    "self": url_for("admin.get_book", id=book.id, _external=True),
                    "edit": url_for("admin.edit_book", id=book.id, _external=True),
                    "delete": url_for("admin.delete_book", id=book.id, _external=True)
                }
            })

        response = make_response(jsonify({
            "books": books_data,
            "_links": {
                "self": url_for("admin.books", _external=True),
                "add": url_for("admin.add_book", _external=True)
            },
            "_meta": {
                "total": len(books_data),
                "cached_at": datetime.utcnow().isoformat() + "Z"
            }
        }), 200)

    
        response.headers["Cache-Control"] = "public, max-age=120"  # cache trong 2 phút
        response.headers["ETag"] = f"W/{len(books_data)}-{books_data[0]['id'] if books_data else 'empty'}"

        return response

    else:
        response = make_response(render_template("admin_books.html", books=books))
        # Cache tạm trong 60s cho giao diện (tùy chọn)
        response.headers["Cache-Control"] = "public, max-age=60"
        return response

@admin_bp.route("/books/<int:id>", methods=["GET"])
def get_book(id):
    book = Book.query.get_or_404(id)
    if request.is_json:
        return jsonify({
            "id": book.id,
            "title": book.title,
            "author": book.author,
            "_links": {
                "self": url_for("admin.get_book", id=id, _external=True),
                "edit": url_for("admin.edit_book", id=id, _external=True),
                "delete": url_for("admin.delete_book", id=id, _external=True),
                "all_books": url_for("admin.books", _external=True)
            }
        })
    else:
        return redirect(url_for("admin.books"))


@admin_bp.route("/books/add", methods=["POST"])
def add_book():
    data = request.get_json(silent=True) or request.form
    title = data.get("title")
    author = data.get("author")
    book = Book(title=title, author=author)
    db.session.add(book)
    db.session.commit()
    if request.is_json:
        return {
            "message": "Book added successfully",
            "book": {"id": book.id, "title": book.title, "author": book.author}
        }, 201
    # Nếu request là form → trả HTML (redirect)
    else:
        return redirect(url_for("admin.books"))

@admin_bp.route("/books/delete/<int:id>", methods=["DELETE", "GET"])
def delete_book(id):
    book = Book.query.get_or_404(id)
    db.session.delete(book)
    db.session.commit()

    if request.is_json or request.method == "DELETE":
        return jsonify({
            "message": f"Book with id={id} deleted successfully",
            "_links": {
                "all_books": url_for("admin.books", _external=True),
                "add_book": url_for("admin.add_book", _external=True)
            }
        }), 200

    return redirect(url_for("admin.books"))

@admin_bp.route("/history")
def history():
    records = BorrowRecord.query.all()
    return render_template("admin_history.html", records=records)

@admin_bp.route("/books/edit/<int:id>", methods=["GET", "POST", "PUT", "PATCH"])
def edit_book(id):
    book = Book.query.get_or_404(id)


    if request.is_json or request.method in ["PUT", "PATCH"]:
        data = request.get_json()
        if "title" in data:
            book.title = data["title"]
        if "author" in data:
            book.author = data["author"]
        db.session.commit()

        return jsonify({
            "message": "Book updated successfully",
            "book": {
                "id": book.id,
                "title": book.title,
                "author": book.author
            },
            "_links": {
                "self": url_for("admin.get_book", id=book.id, _external=True),
                "delete": url_for("admin.delete_book", id=book.id, _external=True),
                "all_books": url_for("admin.books", _external=True)
            }
        }), 200


    elif request.method == "POST":
        book.title = request.form["title"]
        book.author = request.form["author"]
        db.session.commit()
        return redirect(url_for("admin.books"))

    return render_template("admin_edit_book.html", book=book)

@admin_bp.route("/code", methods=["GET"])
def send_dynamic_code():
    # Một đoạn code JavaScript đơn giản
    script = """
    function showWelcomeMessage(username) {
        console.log("Xin chào " + username + "! Đây là code được tải từ server REST (Code on Demand).");
        return "Welcome " + username + "!";
    }
    """
    response = make_response(script)
    response.headers["Content-Type"] = "application/javascript"
    response.headers["Cache-Control"] = "no-cache"
    return response

