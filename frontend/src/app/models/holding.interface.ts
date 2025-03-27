export interface Holding {
    averagePricing: number;
    cryptoCode: string;
    type: string;
    amount: number;
    profit?: number;
    currentValue?: number;
    profitLoss?: number;
}