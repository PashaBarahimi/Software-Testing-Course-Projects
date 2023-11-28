def calculate_total_cost(quantity, unit_price, discount):
    if quantity <= 0 or unit_price <= 0:
        return "Invalid input"
    else:
        total_cost = quantity * unit_price * (1 - discount)
        return total_cost
