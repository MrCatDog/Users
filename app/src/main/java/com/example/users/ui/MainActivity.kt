package com.example.users.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.users.model.domain.FullUserInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.preference.PreferenceManager
import com.example.users.R
import com.example.users.appComponent
import com.example.users.utils.viewModelsExt
import com.example.users.viewmodels.UsersListViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: UsersListViewModel by viewModelsExt {
        baseContext.appComponent.provideUsersListViewModelFactory().create(
            PreferenceManager.getDefaultSharedPreferences(baseContext)
                .getBoolean(UsersListFragment.firstTimeLoadingSharedTag, true)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "usersList") {
                composable("usersList") { MainUsersList { userId: Int -> navController.navigate("profile/$userId") } }
                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    UserProfile(navBackStackEntry.arguments?.getInt("userId")!!) { userId: Int ->
                        navController.navigate("profile/$userId")
                    }
                }
            }
        }
    }

    @Composable
    fun MainUsersList(onNavigateToProfile: (Int) -> Unit) {
        // State
        val users = viewModel.users.observeAsState()

        LaunchedEffect(Unit) {
            viewModel.getUsersList()
        }

        LazyColumn {
            if (users.value != null) {
                items(users.value!!) { user ->
                    UserListItem(user, onNavigateToProfile)
                }
            }
        }
    }

    @Composable
    fun UserProfile(userId: Int, onNavigateToProfile: (Int) -> Unit) {

        val user = viewModel.user.observeAsState().value
        val friends = viewModel.friends.observeAsState()

        LaunchedEffect(Unit) {
            viewModel.getUserInfo(userId)
        }

        if (user != null) {
            Column() {
                Row {
                    Text(text = user.baseUserInfo.name)
                    Text(text = stringResource(id = R.string.user_age_precurse))
                    Text(text = user.age.toString())
                }

                LazyColumn {
                    if (friends.value != null) {
                        items(friends.value!!) { user ->
                            UserListItem(user, onNavigateToProfile)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun UserListItem(user: FullUserInfo.BaseUserInfo, onNavigateToProfile: (Int) -> Unit) {
        Column(
            modifier = Modifier.clickable {
                if (user.isActive) {
                    onNavigateToProfile(user.id)
                }
            }
        ) {
            Text(user.name)
            Text(user.email)
            if (user.isActive) {
                Text(
                    text = stringResource(id = R.string.users_list_isActive_true),
                    color = colorResource(id = R.color.users_list_is_active_true)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.users_list_isActive_false),
                    color = colorResource(id = R.color.users_list_is_active_false)
                )
            }
        }
    }
}