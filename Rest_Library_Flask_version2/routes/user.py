from flask import Blueprint, render_template, request, redirect, url_for
from Rest_Library_Flask_version1.models import db, Book, BorrowRecord
from datetime import datetime

user_bp = Blueprint("user", __name__)

# Giả lập đăng nhập: user_id=2
@user_bp.route("/books")
def books():
    books = Book.query.all()
    return render_template("user_books.html", books=books)

@user_bp.route("/borrow/<int:book_id>", methods=["POST"])
def borrow(book_id):
    user_id = int(request.form["user_id"])
    book = Book.query.get_or_404(book_id)
    if not book.available:
        return "Book not available"
    record = BorrowRecord(borrower_id=user_id, book=book)
    book.available = False
    db.session.add(record)
    db.session.commit()
    return redirect(url_for("user.books"))

@user_bp.route("/return/<int:record_id>")
def return_book(record_id):
    record = BorrowRecord.query.get_or_404(record_id)
    record.return_date = datetime.utcnow()
    record.book.available = True
    db.session.commit()
    return redirect(url_for("user.books"))

@user_bp.route("/history/<int:user_id>")
def history(user_id):
    records = BorrowRecord.query.filter_by(borrower_id=user_id).all()
    return render_template("user_history.html", records=records)
