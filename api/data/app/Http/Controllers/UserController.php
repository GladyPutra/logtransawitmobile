<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Auth;
use Carbon\Carbon;
class UserController extends Controller
{
  public function signup(Request $request)
  {
    $request->validate([
      'username' => 'required|string',
      'email' => 'required|string|email|unique:users',
      'password' => 'required|string|confirmed'
    ]);
    $user = new \App\User([
      'username' => $request->username,
      'email' => $request->email,
      'password' => bcrypt($request->password)
    ]);
    $user->save();
    return response()->json([
      'message' => 'Successfully created user!'
    ], 201);
  }

  /**
  * Login user and create token
  *
  * @param  [string] email
  * @param  [string] password
  * @param  [boolean] remember_me
  * @return [string] access_token
  * @return [string] token_type
  * @return [string] expires_at
  */
  public function login(Request $request)
  {
    $request->validate([
      'username' => 'required|string',
      'password' => 'required|string',
      'remember_me' => 'boolean'
    ]);
    $credentials = [
            'username' => $request->username,
            'password' => $request->password,
            'id_role' => 8
        ];
    if(!Auth::attempt($credentials))
    return response()->json([
      'message' => 'Unauthorized'
    ], 401);
    $user = $request->user();
    $tokenResult = $user->createToken('Personal Access Token');
    $token = $tokenResult->token;
    if ($request->remember_me)
    $token->expires_at = Carbon::now()->addWeeks(1);
    $token->save();
    // return response()->json([
    //   // 'status' => 'success',
    //   // 'access_token' => $tokenResult->accessToken,
    //   // 'token_type' => 'Bearer',
    //   // 'expires_at' => Carbon::parse(
    //   //   $tokenResult->token->expires_at
    //   //   )->toDateTimeString()
    //   $tokenResult->accessToken
    //   ]);
    return $tokenResult->accessToken;
    }

    /**
    * Logout user (Revoke the token)
    *
    * @return [string] message
    */
    public function logout(Request $request)
    {
      $request->user()->token()->revoke();
      return response()->json([
        'message' => 'Successfully logged out'
      ]);
    }

    /**
    * Get the authenticated User
    *
    * @return [json] user object
    */
    public function user(Request $request)
    {
      return $request->user()->id_user;
    }

    public function createdistribusi(Request $r)
    {
      $dp = new \App\DataDistribusi;
      $dp->id_user = $r->id_user;
      $dp->save();

      return "success";
    }

    public function editdistribusi(Request $r)
    {
      // $dp = \App\DataDistribusi::where('id',$r->id_dist)->first();
      // $dp->berat = $r->berat;
      // $dp->save();

      // $tph = \App\TPH::where('id_tph',$r->id_tph)->first();
      // $tph->sisa = $r->sisa;
      // $tph->save();

      $dp = new \App\DataDistribusiPanenTPH;
      $dp->id_distribusi = $r->id_dist;
      $dp->id_tph = $r->id_tph;
      $dp->nb_buah = $r->berat;
      $dp->sisa_buah = $r->sisa;
      $dp->save();

      return "success";
    }

    public function deletedistribusi(Request $r)
    {
      $dp = \App\DataDistribusi::where('id',$r->id_dist)->first();
      $dp->delete();

      $kd = \App\KoordinatDistribusi::where('id_distribusi',$r->id_dist)->delete();

      return "success";
    }

    public function gettph()
    {
      return response()->json(\App\TPH::all());
    }

    public function addcoord(Request $r)
    {
      $kd = new \App\KoordinatDistribusi;
      $kd->id_distribusi = $r->id_dist;
      $kd->latitude = $r->latitude;
      $kd->longitude = $r->longitude;

      $kd->save();

      return "success";
    }

    public function changestatus(Request $r)
    {
      $dp = \App\DataDistribusi::where('id',$r->id_dist)->first();
      $dp->status = 1;
      $dp->save();

      return "success";

    }
  }
