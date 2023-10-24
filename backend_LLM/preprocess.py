# The script will preprocess the amazon dataset on which our LLM model will be trained.
import pandas as pd

# Read the dataset
df = pd.read_csv('All Electronics.csv')

# select a random subset of 1000 rows
df = df.sample(n=2000, random_state=1)

# add new column product_id to both df and df2
df['product_id'] = df.index + 1

print(df.head())

# print all columns
print(df.columns)

# drop all columns except name, ratings and actual price (without inplace=True)
df2 = df.drop(['main_category','sub_category','image','link','no_of_ratings','discount_price'], axis=1)


# export the dataframes to csv files
df.to_csv('./amy_data/app_datasets.csv', index=False)

# create a column product_desciption which combines information from other columns
# df2['product_description'] = df2['name'] + ' has ' + df2['ratings'].astype(str) + ' ratings and costs ' + df2['actual_price'].astype(str) + ' rupees.'
# df2.drop(['name','ratings','actual_price'], axis=1, inplace=True)

#export data in different csv files with max 100 rows in each file

# for i in range(10):
#     df2[i*100:(i+1)*100].to_csv(f'./amy_data/model_datasets_{i+1}.csv', index=False)


for i in range(10):
    with open(f'./amy_data/model_datasets_{i+1}.txt', 'w') as f:
        for row in df2[i*200:(i+1)*200].itertuples():
            txt = f"""INSTRUCTION: Given the product description, find the product_id
            DESCRIPTION: {row.name} and has {row.ratings} ratings and costs {row.actual_price} rupees
            PRODUCT_ID: {row.product_id}
            """
            f.write(txt)