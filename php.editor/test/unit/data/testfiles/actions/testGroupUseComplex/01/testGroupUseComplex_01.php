<?php

namespace A;

const C_A = 'C-A';

function fa() {
    echo __FUNCTION__;
}

class ClsA {

    public function test() {
        echo 'ClsA' . PHP_EOL;
    }

}

class ClsUnused {
}

interface Iface {
}

class ClsAB implements Iface {

}

class MyCls implements Iface {

}

namespace A\B;

const C_B = 'C-B';

function fb() {
    echo __FUNCTION__;
}

class ClsAB {

    public function test() {
        echo 'ClsAB' . PHP_EOL;
    }

}

namespace A\B\C;

const C_C = 'C-C';

function fc() {
    echo __FUNCTION__;
}

class ClsABC {

    public function test() {
        echo 'ClsABC' . PHP_EOL;
    }

}

class ClsABC2 {

    public function test() {
        echo 'ClsABC2' . PHP_EOL;
    }

}

namespace X;

const C_X = 'C-X';

function fx() {
    echo __FUNCTION__;
}

class MyX {
}

namespace Run;

use \X\MyX;
use A\ {
    ClsUnused,
    B\ClsAB
};
use A\B\C\ClsABC,
    \A\B\C\ClsABC2 AS MyCls;
use function X\fx;
use function A\fa,
        \A\B\fb,
        A\B\C\fc;
use const \X\C_X;
use const A\C_A,
        A\B\C_B,
        A\B\C\C_C;
use \A\ClsA;

echo C_A . PHP_EOL;
echo C_B . PHP_EOL;
echo C_C . PHP_EOL;
echo C_X . PHP_EOL;

echo fa() . PHP_EOL;
echo fb() . PHP_EOL;
echo fc() . PHP_EOL;
echo fx() . PHP_EOL;

$a = new ClsA();
$ab = new ClsAB();
$abc = new ClsABC();
$mycls = new MyCls();
$x = new MyX();
