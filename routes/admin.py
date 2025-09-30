from flask import Blueprint, render_template, request, redirect, url_for
from models import db, Book, BorrowRecord

admin_bp = Blueprint("admin", __name__)

@admin_bp.route("/books")
def books():
    books = Book.query.all()
    return render_template("admin_books.html", books=books)

@admin_bp.route("/books/add", methods=["POST"])
def add_book():
    title = request.form["title"]
    author = request.form["author"]
    book = Book(title=title, author=author)
    db.session.add(book)
    db.session.commit()
    return redirect(url_for("admin.books"))

@admin_bp.route("/books/delete/<int:id>")
def delete_book(id):
    book = Book.query.get_or_404(id)
    db.session.delete(book)
    db.session.commit()
    return redirect(url_for("admin.books"))

@admin_bp.route("/history")
def history():
    records = BorrowRecord.query.all()
    return render_template("admin_history.html", records=records)
@admin_bp.route("/books/edit/<int:id>", methods=["GET", "POST"])
def edit_book(id):
    book = Book.query.get_or_404(id)
    if request.method == "POST":
        book.title = request.form["title"]
        book.author = request.form["author"]
        db.session.commit()
        return redirect(url_for("admin.books"))
    return render_template("admin_edit_book.html", book=book)

