package com.bcausic.wealthwise.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.bcausic.wealthwise.data.room.EmailsDatabase
import com.bcausic.wealthwise.helpers.DB_NAME
import com.bcausic.wealthwise.repos.EmailsRepository
import com.bcausic.wealthwise.repos.FirebaseCloudStorageRepository
import com.bcausic.wealthwise.repos.FirebaseAuthRepository
import com.bcausic.wealthwise.repos.FirestoreRepository
import com.bcausic.wealthwise.ui.authentication.AuthenticationViewModel
import com.bcausic.wealthwise.ui.registration.RegistrationViewModel
import com.bcausic.wealthwise.ui.mainscreen.MainFragmentViewModel
import com.bcausic.wealthwise.ui.revenueandexpense.RevenueAndExpenseViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val roomDatabaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            EmailsDatabase::class.java,
            DB_NAME
        ).build()
    }
    single { get<EmailsDatabase>().emailsDao() }
}

val repositoryModule = module {
    single { FirebaseAuthRepository(FirebaseAuth.getInstance()) }
    single { EmailsRepository(get()) }
    single { FirebaseCloudStorageRepository(FirebaseStorage.getInstance()) }
    single { FirestoreRepository(FirebaseFirestore.getInstance()) }
}

val viewModelModule = module {
    viewModel { AuthenticationViewModel(get(), get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel { MainFragmentViewModel(get(), get(), get()) }
    viewModel { RevenueAndExpenseViewModel(get()) }
}