export interface Transaction {
    uuid: string;
    cryptoCode: string;
    tradeType: 'BUY' | 'SELL';
    amount: number;
    fixedPrice: number;
    createdAt: Date;
    profit: number;
}