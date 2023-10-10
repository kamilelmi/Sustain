package com.sustain.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.sustain.MainActivity
import com.sustain.api.SalonAPI
import com.sustain.interfaces.BookingCallback
import com.sustain.interfaces.StripeResultCallback
import com.sustain.model.User
import com.sustain.session.Session
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class StripeHelper()
{
    lateinit var paymentSheet: PaymentSheet
    var userInfo = User()


    fun initPaymentSheet(activity: Fragment, stripeResultCallBack: StripeResultCallback)
    {
        paymentSheet = PaymentSheet(activity){
            when(it) {
                is PaymentSheetResult.Canceled -> {
                    Log.d("MKL","Canceled")
                    stripeResultCallBack.onTransactionFailed()
                }
                is PaymentSheetResult.Failed -> {
                    Log.d("MKL","Error: ${it.error}")
                    Toast.makeText(activity.context,"Transaction Failed! Please try again or use any other payment mode", Toast.LENGTH_SHORT)
                        .show()
                    stripeResultCallBack.onTransactionFailed()
                }
                is PaymentSheetResult.Completed -> {
                    // Display e.g., an order confirmation screen
                    stripeResultCallBack.onTransactionSuccess()
                    Log.d("MKL","Completed")
                }
            }
        }
    }

    fun getStripeCustomerId(context: Activity) {

        val client = OkHttpClient.Builder().addInterceptor(object: Interceptor
        {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${Constants.STRIPE_KEY}")
                    .build()
                return chain.proceed(newRequest)
            }
        })
            .build()


        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.STRIPE_BASE_URL)
            .build()


        userInfo = (context as MainActivity).session()[Session.LOGGED_IN_USER] as User

        val call = retrofit.create(SalonAPI::class.java).createCustomer(
            userInfo.userName
        )

        Log.d("MKL", "Triggering customer request")

        call.enqueue(object : Callback<ResponseBody>
        {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful)
                {
                    val res = response.body()!!.string()
                    Log.d("MKL",res)

                    val json = JSONObject(res)

                    FirebaseDatabase.getInstance().reference
                        .child("user")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("userInfo")
                        .child("stripe_id")
                        .setValue(json.getString("id"))

                    Constants.userObj.stripeId = json.getString("id")


                    Session().add(Session.LOGGED_IN_USER, userInfo)

                }
                else
                {
                    val res = response.errorBody()!!.string()
                    Log.d("MKL", "Response not successful with reason 1\n $res")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("MKL", "CAll Failed with exception: ${t.message}")
            }
        })

    }

    fun getEphemeralKey(callBack: BookingCallback)
    {
        val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${Constants.STRIPE_KEY}")
                .addHeader("Stripe-Version", "2020-08-27").build()
            chain.proceed(newRequest)
        }).build()


        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.STRIPE_BASE_URL)
            .build()


        val call = retrofit.create(SalonAPI::class.java).createCustomerKey(
            Constants.userObj.stripeId
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful)
                {
                    val res = response.body()!!.string()
                    Log.d("MKL",res)

                    val json = JSONObject(res)

                    callBack.onKeyDownloaded(json.getString("secret"))

                }
                else
                {
                    val res = response.errorBody()!!.string()
                    Log.d("MKL", "Response not successful with reason 2 \n $res")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("MKL", "CAll Failed with exception: ${t.message}")
            }
        })

    }

    fun runStripe(amount: Double, callBack: BookingCallback) {


        val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${Constants.STRIPE_KEY}").build()
            chain.proceed(newRequest)
        }).build()


        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.STRIPE_BASE_URL)
            .build()

        val amountToSend = amount * 100

        val call = retrofit.create(SalonAPI::class.java).createPaymentIntent(
            amountToSend.toInt(),
            "gbp",
            Constants.userObj.stripeId
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful)
                {
                    val res = response.body()!!.string()
                    Log.d("MKL",res)

                    val json = JSONObject(res)

                    val paymentIntentClientSecret = json.getString("client_secret")
                    callBack.onKeyDownloaded(paymentIntentClientSecret)

                }
                else
                {
                    val res = response.errorBody()!!.string()
                    Log.d("MKL", "Response not successful with reason 3\n $res")
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("MKL", "CAll Failed with exception: ${t.message}")
            }
        })


    }


}