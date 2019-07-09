package com.globaltics.facturaletrasnew.Clases

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import com.globaltics.facturaletrasnew.R
import java.io.FileNotFoundException
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

class VolleySingleton: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private val requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                return Volley.newRequestQueue(applicationContext, HurlStack(null, getSocketFactory()))
            }
            return field
        }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = TAG
        requestQueue?.add(request)
    }

    companion object {
        private val TAG = VolleySingleton::class.java.simpleName
        @get:Synchronized var instance: VolleySingleton? = null
            private set
    }

    fun getSocketFactory(): SSLSocketFactory? {

        val cf: CertificateFactory?
        try {
            cf = CertificateFactory.getInstance("X.509")
            val caInput = resources?.openRawResource(R.raw.rsgm)
            val ca: Certificate
            try {
                ca = cf!!.generateCertificate(caInput)
                //Log.e("CERT", "ca=" + (ca as X509Certificate).subjectDN)
            } finally {
                caInput?.close()
            }

            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)


            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)


            val hostnameVerifier = HostnameVerifier { hostname, _ ->
                //Log.e("CipherUsed", session.cipherSuite)
                hostname.compareTo("rsgm.online") == 0 //The Hostname of your server
            }


            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
            val context: SSLContext? = SSLContext.getInstance("TLS")

            context!!.init(null, tmf.trustManagers, null)
            HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)


            return context.socketFactory

        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

        return null
    }
}