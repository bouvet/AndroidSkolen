package androidskolen.sesjon1.displayingbitmaps.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidskolen.sesjon1.displayingbitmaps.R

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private var hasShownRationale: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO

        val passwordView = findViewById<EditText>(R.id.password)

        passwordView?.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == 1 || id == EditorInfo.IME_NULL) {
                gotoImageGridActivity()
                return@OnEditorActionListener true
            }
            false
        })

        val emailSignInButton = findViewById<Button>(R.id.email_sign_in_button)
        emailSignInButton?.setOnClickListener { gotoImageGridActivity() }
    }


    /**
     * Implementer denne metoden. Først må det sjekkes at man har rettigheter til å lagre til eksternt minne.
     * Dersom man ikke har det, skal man kalle showExternalStoragePermissionRationale. Dialogen vil kalle
     * gotoImageGridActivity igjen når dialogen lukkes.
     *
     *
     * Når man har vist informasjon om hvorfor man trenger rettigheter, skal man be om rettighetene.
     *
     *
     * Når man har fått rettigheter, skal man lage en intent som åpner ImageGridActivity.
     */
    private fun gotoImageGridActivity() {

        // TODO

    }

    /**
     * Viser en dialog med forklaring til hvorfor man trenger internet permission. Kaller gotoImageGridActivity
     * dersom brukeren trykke "OK".
     */
    private fun showExternalStoragePermissionRationale() {
        hasShownRationale = true
        val builder = AlertDialog.Builder(this)

        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.positive_button) { dialogInterface, i -> gotoImageGridActivity() }.setNegativeButton(R.string.negative_button, null)

        val dialog = builder.create()
        dialog.show()
    }

}

