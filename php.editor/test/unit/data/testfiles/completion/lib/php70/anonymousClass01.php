<?php
namespace My;

$x = new class {
    public function testAnon() {
        $this->testBnon();
    }

    private function testBnon() {
        echo 'testBnon' . PHP_EOL;
    }
};
$x->testAnon();
