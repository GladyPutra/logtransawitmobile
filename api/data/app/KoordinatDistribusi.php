<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class KoordinatDistribusi extends Model
{
  protected $table="koordinat_distribusi";
  protected $primaryKey ="id";
  public $timestamps = false;
  protected $guarded=['id'];
}
