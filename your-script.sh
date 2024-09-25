#!/bin/bash

echo "Starting long computation..."

for i in {1..1000000}
do
# Если счетчик кратен 100000, выводим его значение
if (( $i % 100000 == 0 ))
then
echo "Current count: $i"
fi
done

echo "Computation finished!"