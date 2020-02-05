class CreatePieTypes < ActiveRecord::Migration[6.0]
  def change
    create_table :pie_types do |t|
      t.string :name
      t.float :price

      t.timestamps
    end
  end
end
