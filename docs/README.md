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
* [send -> idle (transition `t_48`)](#transition-t_48)
* [idle -> start (transition `t_19`)](#transition-t_19)
* [(any state) -> start (transition `t_75`)](#transition-t_75)

## transition t_04

### [`2ae6c7859b755abf51a3289b`](../src/main/resources/2ae6c7859b755abf51a3289b.txt)

```
что будем удалять?

```

## transition t_0f

### [`054edccc65c193f7583a5773`](../src/main/resources/054edccc65c193f7583a5773.txt)

```
Введіть адресу в межах Петрівського Кварталу.

```

## transition t_17

### [`5d0c256b4a776245fee81385`](../src/main/resources/5d0c256b4a776245fee81385.txt)

```
введите адрес

```

## transition t_19

### [`fdb3ef9a7dcc8e36c4fa489f`](../src/main/resources/fdb3ef9a7dcc8e36c4fa489f.txt)

```
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_48

### [`6e3ec3f7345ed7115141f355`](../src/main/resources/6e3ec3f7345ed7115141f355.txt)

```
Дякуємо за замовлення!
Якщо у нас будуть питання ми напишемо Вам, або зателефонуємо.

```

## transition t_5e

### [`7a70873a5685da4f9cb2c609`](../src/main/resources/7a70873a5685da4f9cb2c609.txt)

```
Ви замовили {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}.
Сума замовлення: {{order.sum|myprintf}} грн.
Сума за доставку: {{order.delivery_fee|myprintf}} грн.
Сума до сплати: {{(order.sum + order.delivery_fee)|myprintf}} грн.

Що робимо далі?

```

## transition t_72

### [`c9554365515304425712100a`](../src/main/resources/c9554365515304425712100a.txt)

```
{{products[i][2]}}

```

### [`fdb3ef9a7dcc8e36c4fa489f`](../src/main/resources/fdb3ef9a7dcc8e36c4fa489f.txt)

```
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_75

### [`ae784befe1f1bac4d5929a4a`](../src/main/resources/ae784befe1f1bac4d5929a4a.txt)

```
Вітаємо!
Сьогодні ми пропонуємо:

```

### [`fdb3ef9a7dcc8e36c4fa489f`](../src/main/resources/fdb3ef9a7dcc8e36c4fa489f.txt)

```
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_8e

### [`1dc02faec7377fc537510e30`](../src/main/resources/1dc02faec7377fc537510e30.txt)

```
Оберіть форму сплати

```

## transition t_95

### [`a96f38cbc06abbd47de38fe3`](../src/main/resources/a96f38cbc06abbd47de38fe3.txt)

```
Зробіть вибір

```

## transition t_f0

### [`ec779e4315ccf36a38c2d470`](../src/main/resources/ec779e4315ccf36a38c2d470.txt)

```
Ви замовили {{(order.cart|last)['name']}}. Введіть кількість в літрах кратну 0,5 літра.

```

## transition t_f4

### [`67c31fcc0fa6566a955c1792`](../src/main/resources/67c31fcc0fa6566a955c1792.txt)

```
Покласти в кошик

```

## transition t_fa

### [`eb34fa7ee27d1192ef20f960`](../src/main/resources/eb34fa7ee27d1192ef20f960.txt)

```
Ви замовили: {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}
Адреса замовлення: {{order.address}}
Форма сплати: {{order.payment}}
Сума до сплати {{(order.sum + order.delivery_fee)|myprintf}} грн.

```

