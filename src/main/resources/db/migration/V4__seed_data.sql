-- Seed data. INSERT IGNORE means restarting the app won't fail on duplicates.

-- Profiles (advertisers 1-3, publishers 4-5)
INSERT IGNORE INTO profiles (id, email, status, created_at) VALUES
(1, 'contact@fitgear.com',    'ACTIVE', '2026-01-10'),
(2, 'hello@techhub.io',       'ACTIVE', '2026-02-15'),
(3, 'info@greenfoods.com',    'ACTIVE', '2026-03-20'),
(4, 'sara@example.com',       'ACTIVE', '2026-04-01'),
(5, 'youssef@example.com',    'ACTIVE', '2026-04-12');

INSERT IGNORE INTO advertisers (id, company_name, sector, website) VALUES
(1, 'FitGear',    'Sports',     'https://fitgear.com'),
(2, 'TechHub',    'Technology', 'https://techhub.io'),
(3, 'GreenFoods', 'Food',       'https://greenfoods.com');

INSERT IGNORE INTO publishers (id, first_name, last_name, niche, bio, country) VALUES
(4, 'Sara',    'Alaoui', 'SPORT', 'Fitness blogger', 'Morocco'),
(5, 'Youssef', 'Benani', 'TECH',  'Tech reviewer',   'Morocco');

-- Offers
INSERT IGNORE INTO offers (id, advertiser_id, title, description, landing_url, niche, created_at) VALUES
(1, 1, 'Home Gym Bundle',      'Dumbbells and resistance bands', 'https://fitgear.com/bundle',  'fitness',    '2026-05-01 10:00:00.000000'),
(2, 1, 'Protein Powder',       'Whey protein, 2kg',              'https://fitgear.com/protein', 'fitness',    '2026-05-03 11:30:00.000000'),
(3, 2, 'Wireless Earbuds',     'Noise cancelling earbuds',       'https://techhub.io/earbuds',  'technology', '2026-05-10 09:15:00.000000'),
(4, 3, 'Organic Snack Box',    'Monthly healthy snack box',      'https://greenfoods.com/box',  'food',       '2026-05-15 14:00:00.000000');

-- Tags
INSERT IGNORE INTO tags (id, name) VALUES
(1, 'fitness'),
(2, 'health'),
(3, 'gadgets'),
(4, 'organic');

INSERT IGNORE INTO offer_tags (offer_id, tag_id) VALUES
(1, 1), (1, 2),
(2, 1), (2, 2),
(3, 3),
(4, 2), (4, 4);

-- Campaigns (only one ACTIVE per offer)
INSERT IGNORE INTO campaigns (id, offer_id, name, start_date, end_date, budget, payout_model, payout_value, status) VALUES
(1, 1, 'Summer Push',  '2026-06-01', '2026-08-31', 5000.00, 'CPA_PERCENT', 10.00, 'ACTIVE'),
(2, 1, 'Winter Sale',  '2026-11-01', '2026-12-31', 3000.00, 'CPA_PERCENT', 15.00, 'PAUSED'),
(3, 3, 'Launch Promo', '2026-06-15', '2026-09-15', 8000.00, 'CPA_PERCENT',  8.00, 'ACTIVE'),
(4, 4, 'Healthy Start','2026-07-01', '2026-10-01', 2000.00, 'CPA_PERCENT', 12.50, 'ACTIVE');