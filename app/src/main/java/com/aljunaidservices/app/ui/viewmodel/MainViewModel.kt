package com.aljunaidservices.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aljunaidservices.app.data.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://your-api-domain.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(SheetsApiService::class.java)

    private val _dashboardState = MutableStateFlow<UiState<DashboardData>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardData>> = _dashboardState.asStateFlow()

    private val _reviewSubmissionState = MutableStateFlow<UiState<String>?>(null)
    val reviewSubmissionState: StateFlow<UiState<String>?> = _reviewSubmissionState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init { 
        loadAllData() 
    }

    fun loadAllData() {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            try {
                val response = apiService.getDashboardData()
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    _dashboardState.value = UiState.Success(body.data)
                } else {
                    triggerFallback("فشل جلب البيانات من السيرفر. تم تفعيل نظام الاسترجاع التلقائي للبيانات المحلية.")
                }
            } catch (e: Exception) {
                triggerFallback("تعذر الاتصال بالشبكة. يتم الآن عرض البيانات المحلية المخزنة مسبقاً.")
            }
        }
    }

    private fun triggerFallback(noticeMessage: String) {
        _snackbarMessage.value = noticeMessage
        
        val mockSettings = SettingsModel(
            whatsapp_number = "966500123456",
            contact_email = "support@aljunaidservices.com",
            office_address = "المملكة العربية السعودية، الرياض، طريق الملك فهد، برج الفيصلية، الطابق الخامس",
            working_hours = "من الأحد إلى الخميس: 9:00 ص - 8:00 م | السبت: 4:00 م - 9:00 م",
            stats_completed_projects = "1,240+",
            stats_satisfied_clients = "980+",
            stats_years_experience = "8+ سنوات",
            stats_academic_consultants = "15 مستشاراً",
            facebook_link = "https://facebook.com/aljunaidservices",
            twitter_link = "https://twitter.com/aljunaidservices"
        )

        val mockServices = listOf(
            ServiceModel(
                id = "s1",
                title = "الاستشارات الأكاديمية والبحثية",
                description = "تقديم استشارات أكاديمية وبحثية متكاملة لمساعدة الطلاب والباحثين في إعداد خطط البحث واختيار العناوين وصياغة الدراسات والتقارير الأكاديمية وفق المنهجيات العلمية المعتمدة.",
                features = "صياغة مقترح البحث (Proposal), مراجعة وتنسيق المراجع العلمية (APA), التدقيق اللغوي والإملائي الشامل, صياغة وتلخيص الدراسات السابقة بدقة",
                price = "350 ريال سعودي",
                duration = "3 - 5 أيام",
                media = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800"
            ),
            ServiceModel(
                id = "s2",
                title = "التحليل الإحصائي الاستدلالي (SPSS)",
                description = "ترميز وتحليل البيانات الاستبيانية باستخدام حزمة SPSS واستخراج الرسوم البيانية والجداول الإحصائية وتفسير النتائج بكتابة تقرير تحليلي متكامل وشرحه.",
                features = "حساب ثبات وصدق الاستبيان (ألفا كرونباخ), اختبار الفرضيات الإحصائية (T-Test & ANOVA), ترميز وإدخال البيانات بدقة متناهية, تمثيل بياني احترافي للمتغيرات",
                price = "500 ريال سعودي",
                duration = "3 - 4 أيام",
                media = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800"
            ),
            ServiceModel(
                id = "s3",
                title = "تطوير تطبيقات ومشاريع التخرج البرمجية",
                description = "تصميم وتطوير تطبيقات الهواتف الذكية (Android Kotlin) ومواقع الويب لمشاريع التخرج والشركات الناشئة مع شرح تعليمي تفصيلي للأكواد البرمجية.",
                features = "هندسة برمجية متكاملة (MVVM / Clean Arch), واجهات مستخدم متجاوبة (Jetpack Compose), شرح الكود البرمجي عبر Zoom / Meet, دعم فني وتعديلات مجانية لمدة شهر",
                price = "950 ريال سعودي",
                duration = "7 - 12 يوم",
                media = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800"
            )
        )

        val mockPortfolio = listOf(
            PortfolioModel(
                id = "p1",
                title = "تطوير تطبيق عيادتي (Medical Clinic App)",
                description = "تصميم وتطوير تطبيق أندرويد متكامل لحجز مواعيد الأطباء وإدارة الملفات الصحية للمرضى باستخدام Jetpack Compose و Firebase.",
                media = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800",
                type = "image",
                features = "حجز مواعيد فورية مع إرسال تنبيهات, لوحة تحكم كاملة للطاقم الطبي, دعم المحادثات المباشرة بين الطبيب والمريض, واجهات عصرية باللغتين العربية والانجليزية",
                price = "1800 ريال",
                duration = "15 يومًا"
            ),
            PortfolioModel(
                id = "p3",
                title = "فيديو توضيحي: بنية Jetpack Compose",
                description = "فيديو مسجل عالي الجودة يشرح كيفية بناء هيكلية واجهات مستخدم تفاعلية (RTL Layout) وتمرير الحالات باستخدام StateFlow في تطبيقات أندرويد الحديثة.",
                media = "https://www.w3schools.com/html/mov_bbb.mp4",
                type = "video",
                features = "شرح تفصيلي لدورة حياة الكومبوز (Recomposition), آلية تفعيل الوضع المظلم التلقائي, بناء قوائم مخصصة ذات تصفح سلس, مع كود مصدري كامل متاح للتحميل",
                price = "300 ريال",
                duration = "شرح مسجل فوري"
            )
        )

        val mockReviews = listOf(
            ReviewModel("r1", 2, "م. أحمد الشمري", "تعامل راقٍ وسرعة في الإنجاز. تواصلت معهم لمشروع تخرجي في هندسة البرمجيات، وحصلت على كود نظيف جداً مع شرح رائع واجهت به لجنة المناقشة بكل ثقة. أنصح بهم بشدة!", "Approved"),
            ReviewModel("r2", 3, "د. نورة الحربي", "التحليل الإحصائي الذي قاموا به لبحثي العلمي كان في غاية الدقة والمنهجية العلمية. تم قبول البحث في المجلة الدولية بفضل الله ثم بفضل جودة التحليل وتفسير الجداول الإحصائية.", "Approved")
        )

        val mockDashboardData = DashboardData(
            settings = mockSettings,
            services = mockServices,
            portfolio = mockPortfolio,
            reviews = mockReviews
        )

        _dashboardState.value = UiState.Success(mockDashboardData)
    }

    fun submitCustomerReview(name: String, comment: String) {
        viewModelScope.launch {
            _reviewSubmissionState.value = UiState.Loading
            try {
                val request = AddReviewRequest(name = name, comment = comment)
                val response = apiService.submitReview(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _reviewSubmissionState.value = UiState.Success("تم إرسال تقييمك بنجاح وهو بانتظار موافقة الإدارة قبل نشره علناً!")
                    val currentState = _dashboardState.value
                    if (currentState is UiState.Success) {
                        val currentReviews = currentState.data.reviews.toMutableList()
                        currentReviews.add(ReviewModel(
                            id = "temp_${System.currentTimeMillis()}",
                            rowIndex = null,
                            name = name,
                            comment = comment,
                            status = "Pending"
                        ))
                        _dashboardState.value = UiState.Success(currentState.data.copy(reviews = currentReviews))
                    }
                } else {
                    _reviewSubmissionState.value = UiState.Error("عذراً، فشل إرسال التقييم لجداول جوجل. يرجى التحقق من الاتصال بالإنترنت.")
                }
            } catch (e: Exception) {
                _reviewSubmissionState.value = UiState.Error("حدث خطأ أثناء الاتصال: ${e.localizedMessage}")
            }
        }
    }

    fun clearReviewSubmissionState() {
        _reviewSubmissionState.value = null
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
