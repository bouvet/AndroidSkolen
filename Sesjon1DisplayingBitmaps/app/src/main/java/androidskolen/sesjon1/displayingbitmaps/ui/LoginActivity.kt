package androidskolen.sesjon1.displayingbitmaps.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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

        setContentView(R.layout.activity_login)

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

        if (checkExternalStoragePermission()) {
            val intent = Intent(this, ImageGridActivity::class.java)
            intent.putExtra("username", findViewById<EditText>(R.id.email).text.toString());
            intent.putExtra("password", findViewById<EditText>(R.id.password).text.toString());
            startActivity(intent)
        }

    }


    private fun checkExternalStoragePermission() : Boolean {
        val checkSelfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {

            // Har ikke vist "hvorfor"-melding
            if (!hasShownRationale) {

                showExternalStoragePermissionRationale()

            }
            else {

                // Asynkron, callback i "onRequestPermissionResult"
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE)

            }
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // I tilfelle man sender flere forespørsler, sjekk at det er den vi forventer
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {

            if (grantResults.size > 0 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {

                gotoImageGridActivity()

            }

            // Ikke auto-loop tilbake hvis det er DENIED, da et "ikke vis denne meldingen igjen" vil bli ubehagelig
        }
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

    // Definer en konstant som systemet vil svare på requesten med
    companion object {
        val REQUEST_EXTERNAL_STORAGE = 1
    }

}

