<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class DataDistribusi extends Model
{
  protected $table="distribusi_panen";
  protected $primaryKey ="id";
  protected $guarded=['id'];
}
