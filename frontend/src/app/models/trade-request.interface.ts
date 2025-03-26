export interface TradeRequest {
  symbol: string;
  tradeType: 'BUY' | 'SELL';
  fixedPrice: number;
  amount: number;
}
