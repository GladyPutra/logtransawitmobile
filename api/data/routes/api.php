<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/


Route::group(['prefix' => 'auth'], function () {
    Route::post('login', 'UserController@login');
    Route::post('signup', 'UserController@signup');
    Route::post('create-distribusi','UserController@createdistribusi');
    Route::post('addcoord','UserController@addcoord');
    Route::post('changestatus','UserController@changestatus');
    Route::post('editdistribusi','UserController@editdistribusi');
    Route::post('deletedistribusi','UserController@deletedistribusi');
    Route::group([
      'middleware' => 'auth:api'
    ], function() {
        Route::get('logout', 'UserController@logout');
        Route::get('user', 'UserController@user');
        Route::get('gettph', 'UserController@gettph');
        Route::get('newid',function(){
          return \App\DataDistribusi::max('id');
        });
    });
});
