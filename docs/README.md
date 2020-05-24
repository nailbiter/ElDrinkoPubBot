
# docs for `ElDrinkoPubBot`

## state diagram

![](./states.svg)

## transitions

* [0 -> 10 (transition 0)](#transition-0)
* [10 -> 5 (transition 1)](#transition-1)
* [5 -> 10 (transition 2)](#transition-2)
* [10 -> 4 (transition 3)](#transition-3)
* [4 -> 2 (transition 4)](#transition-4)
* [2 -> 6 (transition 5)](#transition-5)
* [6 -> 4 (transition 6)](#transition-6)
* [6 -> 7 (transition 7)](#transition-7)
* [7 -> 6 (transition 8)](#transition-8)
* [6 -> 1 (transition 9)](#transition-9)
* [1 -> 3 (transition 10)](#transition-10)
* [3 -> 9 (transition 11)](#transition-11)
* [9 -> 8 (transition 12)](#transition-12)
* [8 -> 9 (transition 13)](#transition-13)
* [9 -> 3 (transition 14)](#transition-14)
* [9 -> 10 (transition 15)](#transition-15)


## transition 0

```
Вітаємо!
Сьогодні ми пропонуємо:
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн./л.)
{%-endfor%}

```
## transition 1

```
(кнопки с продуктами)
```
## transition 2

```
(описание продукта с изображением)
```

```
Вітаємо!
Сьогодні ми пропонуємо:
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн./л.)
{%-endfor%}

```
## transition 3

```
Покласти в корзину

```
## transition 4

```
Ви замовили {{(order.cart|last)['name']}}. Введіть кількість в літрах (кратну 0,5 літра).

```
## transition 5

```
Ви замовили {{(order.cart|last)['amount']}} л {{(order.cart|last)['name']}}.
Сума замовлення: {{order.sum}} грн.
Сума за доставку: {{order.delivery_fee}} грн.
Сума до сплати: {{order.sum + order.delivery_fee}} грн.

Що робимо далі?

```
## transition 6

```
давайте добавим еще
```
## transition 7

```
что будем удалять?
```
## transition 8

```
удалили "%s". Что дальше?
```
## transition 9

```
Введіть адресу в межах Петрівському кварталу.

```
## transition 10

```
Оберіть форму сплати

```
## transition 11

```
Ви замовили: {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}
Адреса замовлення: {{order.address}}
Форма сплати: {{order.payment}}
Сумма до сплати {{order.sum + order.delivery_fee}} грн.

Дякуємо за замовлення!
Якщо у нас будуть питання ми напишемо Вам, або зателефонуємо.

```
## transition 12

```
введите адрес
```
## transition 13

```
Ви замовили: {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}
Адреса замовлення: {{order.address}}
Форма сплати: {{order.payment}}
Сумма до сплати {{order.sum + order.delivery_fee}} грн.

Дякуємо за замовлення!
Якщо у нас будуть питання ми напишемо Вам, або зателефонуємо.

```
## transition 14

```
Оберіть форму сплати

```
## transition 15

```
Замовлення №{{order.count}}; {{order.timestamp}}
Покупець: {{order.uid}}
Замовив: {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}
Адреса замовлення: {{order.address}}
Форма сплати: {{order.payment}}
Сумма до сплати {{order.sum + order.delivery_fee}} грн.

```

