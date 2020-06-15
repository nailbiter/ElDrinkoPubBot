# docs for `ElDrinkoPubBot`

## state diagram

![](./states.svg)

## transitions

* [_ -> start (transition `t_75f`)](#transition-t_75f)
* [start -> choose_product_to_see_description (transition `t_950`)](#transition-t_950)
* [choose_product_to_see_description -> start (transition `t_72a`)](#transition-t_72a)
* [start -> choose_product_to_make_order (transition `t_f4a`)](#transition-t_f4a)
* [choose_product_to_make_order -> choose_amount (transition `t_f02`)](#transition-t_f02)
* [choose_amount -> confirm (transition `t_5e1`)](#transition-t_5e1)
* [confirm -> choose_product_to_make_order (transition `t_f4a`)](#transition-t_f4a)
* [confirm -> delete (transition `t_04d`)](#transition-t_04d)
* [delete -> confirm (transition `t_5e1`)](#transition-t_5e1)
* [confirm -> choose_address (transition `t_0f1`)](#transition-t_0f1)
* [choose_address -> choose_phone_number (transition `t_72e`)](#transition-t_72e)
* [choose_phone_number -> choose_payment (transition `t_8e0`)](#transition-t_8e0)
* [choose_payment -> send (transition `t_fa7`)](#transition-t_fa7)
* [send -> edit_address (transition `t_177`)](#transition-t_177)
* [edit_address -> send (transition `t_fa7`)](#transition-t_fa7)
* [send -> choose_payment (transition `t_8e0`)](#transition-t_8e0)
* [send -> idle (transition `t_48c`)](#transition-t_48c)
* [idle -> start (transition `t_19e`)](#transition-t_19e)
* [(any state) -> start (transition `t_75f`)](#transition-t_75f)

## transition t_04d

### [`2ae6c7859b755abf51a3289b`](../src/main/resources/2ae6c7859b755abf51a3289b.txt)

```
что будем удалять?

```

## transition t_0f1

### [`054edccc65c193f7583a5773`](../src/main/resources/054edccc65c193f7583a5773.txt)

```
Введіть адресу в межах Петрівського Кварталу.

```

## transition t_177

### [`5d0c256b4a776245fee81385`](../src/main/resources/5d0c256b4a776245fee81385.txt)

```
введите адрес

```

## transition t_19e

### [`fdb3ef9a7dcc8e36c4fa489f`](../src/main/resources/fdb3ef9a7dcc8e36c4fa489f.txt)

```
Сьогодні ми пропонуємо:
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_48c

### [`6e3ec3f7345ed7115141f355`](../src/main/resources/6e3ec3f7345ed7115141f355.txt)

```
Дякуємо за замовлення!
Ми опрацюємо його, якомога швидше.
Якщо у нас виникнуть питання ми напишемо Вам, або зателефонуємо.

```

## transition t_5e1

### [`7a70873a5685da4f9cb2c609`](../src/main/resources/7a70873a5685da4f9cb2c609.txt)

```
Ви замовили {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}.
Сума замовлення: {{order.sum|myprintf}} грн.
Сума за доставку: {{order.delivery_fee|myprintf}} грн.
Сума до сплати: {{(order.sum + order.delivery_fee)|myprintf}} грн.

Що робимо далі?

```

## transition t_72a

### [`c9554365515304425712100a`](../src/main/resources/c9554365515304425712100a.txt)

```
{{products[i][2]}}

```

### [`fdb3ef9a7dcc8e36c4fa489f`](../src/main/resources/fdb3ef9a7dcc8e36c4fa489f.txt)

```
Сьогодні ми пропонуємо:
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_72e

### [`fcff2695d806371dab5d7e05`](../src/main/resources/fcff2695d806371dab5d7e05.txt)

```
Введiть номер телефону (тiльки цифри)

```

## transition t_75f

### [`ae784befe1f1bac4d5929a4a`](../src/main/resources/ae784befe1f1bac4d5929a4a.txt)

```
Вітаємо!

```

### [`fdb3ef9a7dcc8e36c4fa489f`](../src/main/resources/fdb3ef9a7dcc8e36c4fa489f.txt)

```
Сьогодні ми пропонуємо:
{%-for r in products%}
{{(r[0]|int) + 1}}. {{r[1]}} ({{r[3]}}.00 грн/л)
{%-endfor%}

```

## transition t_8e0

### [`1dc02faec7377fc537510e30`](../src/main/resources/1dc02faec7377fc537510e30.txt)

```
Оберіть форму сплати

```

## transition t_950

### [`a96f38cbc06abbd47de38fe3`](../src/main/resources/a96f38cbc06abbd47de38fe3.txt)

```
Зробіть вибір

```

## transition t_f02

### [`ec779e4315ccf36a38c2d470`](../src/main/resources/ec779e4315ccf36a38c2d470.txt)

```
Ви замовили {{(order.cart|last)['name']}}. Введіть кількість в літрах кратну 0,5 літра.

```

## transition t_f4a

### [`67c31fcc0fa6566a955c1792`](../src/main/resources/67c31fcc0fa6566a955c1792.txt)

```
Покласти в кошик

```

## transition t_fa7

### [`eb34fa7ee27d1192ef20f960`](../src/main/resources/eb34fa7ee27d1192ef20f960.txt)

```
Ви замовили: {%for r in order.cart%}{{r.name}}-{{r.amount}} л{%if not loop.last%}; {%endif%}{%endfor%}
Адреса замовлення: {{order.address}}
Форма сплати: {{order.payment}}
Сума до сплати {{(order.sum + order.delivery_fee)|myprintf}} грн.

```

