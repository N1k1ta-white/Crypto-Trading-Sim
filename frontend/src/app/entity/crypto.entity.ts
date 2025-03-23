
export interface Crypto {
    symbol: string;
    bid: number;
    bidQty: number;
    ask: number;
    askQty: number;
    last: number;
    volume: number;
    vwap: number;
    low: number;
    high: number;
    change: number;
    changePct: number;
}