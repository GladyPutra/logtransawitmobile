<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class DataDistribusiPanenTPH extends Model
{
  protected $table='distribusi_panen_tph';
  public $incrementing = false;
  protected $fillable = ['id_distribusi','id_tph','nb_buah','sisa_buah'];
  public $timestamps = false;
}
