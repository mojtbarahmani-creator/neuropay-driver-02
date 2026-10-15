package ir.neuropay.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private var balance = 1_000_000L
    private lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun setup(title: String) {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(Color.rgb(245, 248, 250))
        }
        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
        addText(title, 26, Color.rgb(20, 45, 75))
    }

    private fun addText(
        value: String,
        size: Int = 18,
        color: Int = Color.BLACK
    ) {
        root.addView(TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            gravity = Gravity.RIGHT
            setPadding(4, 12, 4, 12)
        })
    }

    private fun addButton(label: String, action: () -> Unit) {
        root.addView(Button(this).apply {
            text = label
            isAllCaps = false
            setOnClickListener { action() }
        })
    }

    private fun message(text: String) {
        AlertDialog.Builder(this)
            .setTitle("نوروپی")
            .setMessage(text)
            .setPositiveButton("باشه", null)
            .show()
    }

    private fun showHome() {
        setup("نوروپی")
        addText("موجودی آزمایشی کیف پول")
        addText("%,d تومان".format(balance), 30, Color.rgb(0, 120, 110))
        addText("نسخه نمایشی؛ انتقال واقعی پول انجام نمی‌شود.", 14)
        addButton("پرداخت آزمایشی") { showPayment() }
        addButton("واریز به حساب جهت برداشت پول از کیف پول") {
            showWithdrawal()
        }
        addButton("تاریخچه تراکنش‌ها") {
            setup("تاریخچه تراکنش‌ها")
            addText("تاریخچه در این نسخه ذخیره دائمی نمی‌شود.")
            addButton("بازگشت") { showHome() }
        }
    }

    private fun showPayment() {
        setup("پرداخت آزمایشی")
        val receiver = EditText(this).apply {
            hint = "نام پذیرنده"
            setText("پذیرنده آزمایشی")
        }
        val amount = EditText(this).apply {
            hint = "مبلغ به تومان"
            inputType = 2
        }
        root.addView(receiver)
        root.addView(amount)

        addButton("ثبت پرداخت آزمایشی") {
            val value = amount.text.toString().toLongOrNull()
            when {
                value == null || value <= 0 ->
                    message("مبلغ معتبر وارد کن.")
                value > balance ->
                    message("موجودی کافی نیست.")
                else -> AlertDialog.Builder(this)
                    .setTitle("تأیید پرداخت")
                    .setMessage("پرداخت آزمایشی $value تومان ثبت شود؟")
                    .setNegativeButton("لغو", null)
                    .setPositiveButton("تأیید") { _, _ ->
                        balance -= value
                        message("پرداخت فقط در نسخه آزمایشی ثبت شد.")
                        showHome()
                    }
                    .show()
            }
        }
        addButton("بازگشت") { showHome() }
    }

    private fun showWithdrawal() {
        setup("برداشت از کیف پول")
        addText("واریز به حساب جهت برداشت پول از کیف پول")
        addText("موجودی نمایشی: %,d تومان".format(balance))

        val amount = EditText(this).apply {
            hint = "مبلغ برداشت به تومان"
            inputType = 2
        }
        val sheba = EditText(this).apply {
            hint = "شماره شبا: IR و ۲۴ رقم"
        }
        root.addView(amount)
        root.addView(sheba)

        addButton("ثبت درخواست آزمایشی برداشت") {
            val value = amount.text.toString().toLongOrNull()
            val iban = sheba.text.toString()
                .replace(" ", "")
                .uppercase()

            when {
                value == null || value <= 0 ->
                    message("مبلغ معتبر وارد کن.")
                value > balance ->
                    message("موجودی کافی نیست.")
                !iban.matches(Regex("IR[0-9]{24}")) ->
                    message("شماره شبا باید با IR و ۲۴ رقم باشد.")
                else ->
                    message(
                        "فرم آزمایشی بررسی شد. پولی به بانک ارسال نشده و موجودی تغییر نکرده است."
                    )
            }
        }
        addButton("بازگشت") { showHome() }
    }
}
