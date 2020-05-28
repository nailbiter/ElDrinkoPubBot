# docs for `ElDrinkoPubBot`

## state diagram

![](./states.svg)

## transitions

* [_ -> start (transition `t_75`)](#transition-t_75)
* [start -> choose_product_to_see_description (transition `t_95`)](#transition-t_95)
* [choose_product_to_see_description -> start (transition `t_72`)](#transition-t_72)
* [start -> choose_product_to_make_order (transition `t_f4`)](#transition-t_f4)
* [choose_product_to_make_order -> choose_amount (transition `t_f0`)](#transition-t_f0)
* [choose_amount -> confirm (transition `t_5e`)](#transition-t_5e)
* [confirm -> choose_product_to_make_order (transition `t_f4`)](#transition-t_f4)
* [confirm -> delete (transition `t_04`)](#transition-t_04)
* [delete -> confirm (transition `t_5e`)](#transition-t_5e)
* [confirm -> choose_address (transition `t_0f`)](#transition-t_0f)
* [choose_address -> choose_payment (transition `t_8e`)](#transition-t_8e)
* [choose_payment -> send (transition `t_fa`)](#transition-t_fa)
* [send -> edit_address (transition `t_17`)](#transition-t_17)
* [edit_address -> send (transition `t_fa`)](#transition-t_fa)
* [send -> choose_payment (transition `t_8e`)](#transition-t_8e)
* [send -> start (transition `t_48`)](#transition-t_48)

## transition t_04

### `2ae6c7859b755abf51a3289b`

```
что будем удалять?

```

## transition t_0f

### `054edccc65c193f7583a5773`

```
Введіть адресу в межах Петрівського Кварталу.

```

## transition t_17

### `5d0c256b4a776245fee81385`

```
введите адрес

```

## transition t_48

### `fdb3ef9a7dcc8e36c4fa489f`

```
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_5e

### `7a70873a5685da4f9cb2c609`

```
Ви замовили {{(order.cart|last)['amount']}} л {{(order.cart|last)['name']}}.
Сума замовлення: {{order.sum}} грн.
Сума за доставку: {{order.delivery_fee}} грн.
Сума до сплати: {{order.sum + order.delivery_fee}} грн.

Що робимо далі?

```

## transition t_72

### `c9554365515304425712100a`

```
{{products[i].description}}

```

### `fdb3ef9a7dcc8e36c4fa489f`

```
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_75

### `ae784befe1f1bac4d5929a4a`

```
Вітаємо!
Сьогодні ми пропонуємо:

```

### `fdb3ef9a7dcc8e36c4fa489f`

```
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_8e

### `1dc02faec7377fc537510e30`

```
Оберіть форму сплати

```

## transition t_95

### `a96f38cbc06abbd47de38fe3`

```
Зробіть вибір

```

## transition t_f0

### `ec779e4315ccf36a38c2d470`

```
Ви замовили {{(order.cart|last)['name']}}. Введіть кількість в літрах кратну 0,5 літра.

```

## transition t_f4

### `67c31fcc0fa6566a955c1792`

```
Покласти в корзину

```

## transition t_fa

### `eb34fa7ee27d1192ef20f960`

```
Ви замовили: {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}
Адреса замовлення: {{order.address}}
Форма сплати: {{order.payment}}
Сумма до сплати {{order.sum + order.delivery_fee}} грн.

Дякуємо за замовлення!
Якщо у нас будуть питання ми напишемо Вам, або зателефонуємо.

```

