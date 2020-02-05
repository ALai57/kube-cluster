class OrdersController < ApplicationController
  def new
    @pie_types = PieType.all
    @order = Order.new()
  end

  def create
    puts "Hello! create"
    @order = Order.new(order_params)
    puts @order
    @order.save
    redirect_to @order
    # render plain: params[:order].inspect
  end

  def show
    @order = Order.find(params[:id])
  end

  def index
    @orders = Order.all
  end

  private
  def order_params
    params.require(:order).permit!

    total = calculate_total_price(params[:order])

    {"details" => params[:order],
     "total" => total}
  end

  def calculate_total_price(order)
    @pie_types = PieType.all

    pie_type_to_price = {}
    @pie_types.entries.each do |pie|
      pie_type_to_price[pie.name] = pie.price
    end

    total = 0
    order.each do |pie_type, qty|
      total +=  pie_type_to_price[pie_type] * qty.to_i
    end
    total
  end
end
